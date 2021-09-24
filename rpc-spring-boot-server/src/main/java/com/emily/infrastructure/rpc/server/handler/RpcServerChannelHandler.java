package com.emily.infrastructure.rpc.server.handler;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;
import com.emily.infrastructure.rpc.server.registry.RpcProviderRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/17
 */
public class RpcServerChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerChannelHandler.class);
    /**
     * RPC服务注册中心
     */
    private RpcProviderRegistry registry;

    public RpcServerChannelHandler(RpcProviderRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("RPC服务器连接断开：{}-{}", ctx.channel().id(), ctx.channel().isActive());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    System.out.println("------------READER_IDLE");
                    break;
                case WRITER_IDLE:
                    System.out.println("------------WRITER_IDLE");
                    break;
                case ALL_IDLE:
                    System.out.println("------------ALL_IDLE");
                    break;
                default:
                    break;
            }
        }
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
        logger.info("接收到的数据是：{}", msg);
        if (msg == null) {
            return;
        }
        RpcRequest rpcRequest = (RpcRequest) msg;
        //反射调用实现类的方法
        String className = rpcRequest.getClassName();
        //从注册表中获取指定名称的实现类
        Class<?> aClass = registry.getServiceBean(className).getClass();
        Object o = aClass.getDeclaredConstructor().newInstance();
        Method method = aClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getTypes());
        method.setAccessible(true);
        Object invoke = method.invoke(o, rpcRequest.getParams());
        ctx.writeAndFlush(new RpcResponse(rpcRequest.getTraceId(), invoke));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error(PrintExceptionInfo.printErrorInfo(cause));
    }
}
