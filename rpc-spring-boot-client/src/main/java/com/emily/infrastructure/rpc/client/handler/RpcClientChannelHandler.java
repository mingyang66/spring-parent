package com.emily.infrastructure.rpc.client.handler;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: spring-parent
 * @description: 由于需要在 handler 中发送消息给服务端，并且将服务端返回的消息读取后返回给消费者,所以实现了 Callable 接口，这样可以运行有返回值的线程
 * @author: Emily
 * @create: 2021/09/17
 */
public class RpcClientChannelHandler extends BaseClientHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientChannelHandler.class);

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
            response = (RpcResponse) msg;
            this.object.notify();
            logger.info("RPC响应数据：{}  ", JSONUtils.toJSONString(msg));
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
        logger.error(PrintExceptionInfo.printErrorInfo(cause));
        super.exceptionCaught(ctx, cause);
    }
}
