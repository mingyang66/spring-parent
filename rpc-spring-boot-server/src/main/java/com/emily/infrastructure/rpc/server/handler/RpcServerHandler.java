package com.emily.infrastructure.rpc.server.handler;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.common.entity.ClassInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/17
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    public static Map<String, Class<?>> classNameMapping = new HashMap();

    public static void setClassNameMapping(Object object) {
        Class<?> clazz = object.getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        Class<?> anInterface = interfaces[0];
        //为实现类定义标识，方便客户端和服务端通信调用
        classNameMapping.put(anInterface.getSimpleName(), object.getClass());
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
        ClassInfo classInfo = JSONUtils.toJavaBean((String) msg, ClassInfo.class);
        //确认是rpc调用才往下执行
        if (!StringUtils.equals(ClassInfo.PROTOCOL, classInfo.getProtocol())) {
            return;
        }
        //反射调用实现类的方法
        String className = classInfo.getClassName();
        //获取指定名称的实现类
        Class<?> aClass = classNameMapping.get(className);
        Object o = aClass.getDeclaredConstructor().newInstance();
        if (classInfo.getTypes().length > 0) {
            Method method = aClass.getMethod(classInfo.getMethodName(), classInfo.getTypes());
            method.setAccessible(true);
            Object invoke = method.invoke(o, classInfo.getParams());
            ctx.writeAndFlush(JSONUtils.toJSONString(invoke));
        } else {
            Method method = aClass.getMethod(classInfo.getMethodName());
            method.setAccessible(true);
            Object invoke = method.invoke(o);
            ctx.writeAndFlush(JSONUtils.toJSONString(invoke));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error(PrintExceptionInfo.printErrorInfo(cause));
    }
}
