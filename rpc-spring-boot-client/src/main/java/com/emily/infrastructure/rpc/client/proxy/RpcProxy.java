package com.emily.infrastructure.rpc.client.proxy;

import com.emily.infrastructure.core.ioc.IOCContext;
import com.emily.infrastructure.rpc.core.client.handler.RpcProxyHandler;
import com.emily.infrastructure.rpc.core.client.handler.RpcClientInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

/**
 * @program: spring-parent
 * @description: 创建Netty客户端及自定义处理器
 * @author: Emily
 * @create: 2021/09/17
 */
public class RpcProxy {

    private static final Logger logger = LoggerFactory.getLogger(RpcProxy.class);



    /**
     * 获取一个动态代理对象
     *
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> target) {
        RpcProxyHandler rpcClientHandler = IOCContext.getBean(RpcProxyHandler.class);
        RpcClientInvocationHandler handler = new RpcClientInvocationHandler(rpcClientHandler, target.getSimpleName());
        // 获取class对象接口实例对象
        Class<?>[] interfaces = target.isInterface() ? new Class<?>[]{target} : target.getInterfaces();
        return (T) Proxy.newProxyInstance(target.getClassLoader(), interfaces, handler);
    }

}
