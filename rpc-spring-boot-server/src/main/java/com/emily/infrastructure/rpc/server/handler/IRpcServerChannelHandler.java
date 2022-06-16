package com.emily.infrastructure.rpc.server.handler;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.core.message.IRpcMessage;
import com.emily.infrastructure.rpc.core.message.IRpcRequest;
import com.emily.infrastructure.rpc.core.message.IRpcResponse;
import com.emily.infrastructure.rpc.server.logger.RecordLogger;
import com.emily.infrastructure.rpc.server.registry.IRpcProviderRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
        //请求协议
        IRpcRequest request = null;
        //返回结果
        IRpcResponse rpcResponse = null;
        try {
            //请求消息
            IRpcMessage message = (IRpcMessage) msg;
            //消息类型
            byte packageType = message.getPackageType();
            //心跳包
            if (packageType == 1) {
                String heartBeat = new String(message.getBody(), StandardCharsets.UTF_8);
                logger.info("通道{}的心跳包是：{}", ctx.channel().remoteAddress(), heartBeat);
                return;
            }
            //请求协议
            request = JSONUtils.toObject(message.getBody(), IRpcRequest.class);
            //反射调用实现类的方法
            String className = request.getClassName();
            //从注册表中获取指定名称的实现类
            Object serviceBean = registry.getServiceBean(className);
            //获取实现类的class实例
            Class<?> clazz = serviceBean.getClass();
            //获取实现类的bean对象
            Object bean = clazz.getDeclaredConstructor().newInstance();
            //获取实现类的Method对象
            Method method = clazz.getMethod(request.getMethodName(), request.getTypes());
            //设置方法访问权限为true
            method.setAccessible(true);
            //将参数转换为真实数据类型
            Object[] parameters = getParameters(request.getTypes(), request.getParams());
            //调用具体实现方法
            Object response = method.invoke(bean, parameters);
            //Rpc响应结果
            rpcResponse = IRpcResponse.buildResponse(response);
        } catch (Exception ex) {
            //异常结果
            Object response = PrintExceptionInfo.printErrorInfo(ex);
            //Rpc响应结果
            rpcResponse = IRpcResponse.buildResponse(AppHttpStatus.NETWORK_EXCEPTION.getStatus(), AppHttpStatus.NETWORK_EXCEPTION.getMessage(), response);
        } finally {
            //设置请求上下文的事物唯一标识
            if (Objects.nonNull(request)) {
                rpcResponse.setTraceId(request.getTraceId());
            }
            //发送调用方法调用结果
            ctx.writeAndFlush(IRpcMessage.build(JSONUtils.toByteArray(rpcResponse)));
            //手动释放消息，否则会导致内存泄漏
            ReferenceCountUtil.release(msg);
            //记录请求相依日志
            RecordLogger.recordResponse(request, rpcResponse, startTime);
        }
    }

    /**
     * 将参数转换为真实的数据类型
     *
     * @param parameterTypes 参数类型数组
     * @param parameters     参数数组
     * @return
     */
    private Object[] getParameters(Class<?>[] parameterTypes, Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return parameters;
        } else {
            Object[] newParameters = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                newParameters[i] = JSONUtils.parseObject(parameters[i], parameterTypes[i]);
            }
            return newParameters;
        }
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
