package com.emily.infrastructure.rpc.server.handler;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcBody;
import com.emily.infrastructure.rpc.server.registry.RpcRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
    private RpcRegistry registry;

    public RpcServerChannelHandler(RpcRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("Rpc客户端连接成功：{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("RPC服务器连接断开：{}", ctx.channel().remoteAddress());
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
        logger.info("接收到的数据是：{}", JSONUtils.toJSONString(msg));
        if (msg == null) {
            return;
        }
        try {
            RpcRequest request = (RpcRequest) msg;
            //反射调用实现类的方法
            String className = request.getClassName();
            //从注册表中获取指定名称的实现类
            Object serviceBean = registry.getServiceBean(className);
            Class<?> aClass = serviceBean.getClass();
            Object o = aClass.getDeclaredConstructor().newInstance();
            Method method = aClass.getMethod(request.getMethodName(), request.getTypes());
            method.setAccessible(true);

            Object invoke = method.invoke(o, request.getParams());
            ctx.writeAndFlush(RpcBody.toBody(request.getTraceId(), invoke));
        } catch (Exception ex){
            logger.error(PrintExceptionInfo.printErrorInfo(ex));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error(PrintExceptionInfo.printErrorInfo(cause));
    }
}
