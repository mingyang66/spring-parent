package com.emily.infrastructure.rpc.client.handler;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.core.entity.message.IRBody;
import com.emily.infrastructure.rpc.core.entity.message.IRMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @program: spring-parent
 * @description: 由于需要在 handler 中发送消息给服务端，并且将服务端返回的消息读取后返回给消费者,所以实现了 Callable 接口，这样可以运行有返回值的线程
 * @author: Emily
 * @create: 2021/09/17
 */
@ChannelHandler.Sharable
public class IRpcClientChannelHandler extends BaseClientHandler {

    private static final Logger logger = LoggerFactory.getLogger(IRpcClientChannelHandler.class);

    /**
     * 实现channelRead 当我们读到服务器数据,该方法自动执行
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        synchronized (this.object) {
            //将消息对象转换为指定消息体
            IRMessage message = (IRMessage) msg;
            //将真实的消息体转换为字符串类型
            this.response = new String(message.getBody().getData(), StandardCharsets.UTF_8);
            //唤醒等待线程
            this.object.notify();
            logger.info("RPC响应数据：{}  ", JSONUtils.toJSONString(this.response));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("连接{}已经超过20秒未与服务端进行读写操作，经发送心跳消息...", ctx.channel().remoteAddress());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                case WRITER_IDLE:
                case ALL_IDLE:
                    IRMessage message = new IRMessage();
                    //设置包类型为心跳包
                    message.getHead().setPackageType(1);
                    message.setBody(IRBody.toBody("heartBeat".getBytes(StandardCharsets.UTF_8)));
                    ctx.channel().writeAndFlush(message);
                    break;
                default:
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error(PrintExceptionInfo.printErrorInfo(cause));
    }

}
