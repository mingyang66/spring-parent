package com.emily.infrastructure.rpc.server.handler;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.core.entity.message.IRBody;
import com.emily.infrastructure.rpc.core.entity.message.IRHead;
import com.emily.infrastructure.rpc.core.entity.message.IRMessage;
import com.emily.infrastructure.rpc.core.entity.protocol.IRProtocol;
import com.emily.infrastructure.rpc.server.logger.RecordLogger;
import com.emily.infrastructure.rpc.server.registry.IRpcProviderRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/17
 */
public class IRpcServerChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(IRpcServerChannelHandler.class);
    /**
     * RPC服务注册中心
     */
    private IRpcProviderRegistry registry;

    public IRpcServerChannelHandler(IRpcProviderRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("Rpc客户端连接成功：{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Rpc服务器连接断开：{}", ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    /**
     * 接收客户端传入的值，将值解析为类对象，获取其中的属性，然后反射调用实现类的方法
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        //开始时间
        long startTime = System.currentTimeMillis();
        //消息
        IRMessage message = (IRMessage) msg;
        //消息类型
        int packageType = message.getHead().getPackageType();
        //心跳包
        if (packageType == 1) {
            String heartBeat = new String(message.getBody().getData(), StandardCharsets.UTF_8);
            logger.info("通道{}的心跳包是：{}...", ctx.channel().remoteAddress(), heartBeat);
            return;
        }
        IRProtocol protocol = JSONUtils.toObject(message.getBody().getData(), IRProtocol.class);
        //反射调用实现类的方法
        String className = protocol.getClassName();
        //从注册表中获取指定名称的实现类
        Object serviceBean = registry.getServiceBean(className);
        //获取实现类的class实例
        Class<?> aClass = serviceBean.getClass();
        //获取实现类的bean对象
        Object bean = aClass.getDeclaredConstructor().newInstance();
        //获取实现类的Method对象
        Method method = aClass.getMethod(protocol.getMethodName(), protocol.getTypes());
        //设置方法访问权限为true
        method.setAccessible(true);
        //调用具体实现方法
        Object response = method.invoke(bean, protocol.getParams());
        //返回方法调用结果
        ctx.writeAndFlush(new IRMessage(new IRHead(message.getHead().getTraceId()), IRBody.toBody(response)));
        //记录请求相依日志
        RecordLogger.recordResponse(message.getHead(), protocol, response, startTime);
    }

    /**
     * 服务端时间触发，心跳包
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                case WRITER_IDLE:
                case ALL_IDLE:
                    logger.info("客户端已经超过60秒未读写数据，关闭连接{}。", ctx.channel().remoteAddress());
                    ctx.channel().close();
                    break;
                default:
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error(PrintExceptionInfo.printErrorInfo(cause));
    }
}
