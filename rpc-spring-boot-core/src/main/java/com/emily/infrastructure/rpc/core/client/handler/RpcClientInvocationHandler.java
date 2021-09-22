package com.emily.infrastructure.rpc.core.client.handler;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Future;

/**
 * @program: spring-parent
 * @description: RPC客户端动态代理调用处理程序
 * @author: Emily
 * @create: 2021/09/22
 */
public class RpcClientInvocationHandler implements InvocationHandler {
    private RpcClientChannelHandler rpcClientChannelHandler;
    private String className;

    public RpcClientInvocationHandler(RpcClientChannelHandler rpcClientChannelHandler, String className) {
        this.rpcClientChannelHandler = rpcClientChannelHandler;
        this.className = className;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //组装传输类的属性值
        RpcRequest invokerProtocol = new RpcRequest(className, method.getName(), method.getParameterTypes(), args);
        rpcClientChannelHandler.setInvokerProtocol(invokerProtocol);
        //运行线程，发送数据
        Future future = ThreadPoolHelper.threadPoolTaskExecutor().submit(rpcClientChannelHandler);
        //返回结果
        String o1 = (String) future.get();
        //获取返回类型，并将服务端返回的json数据转化为对应的类型
        Type returnType = method.getAnnotatedReturnType().getType();
        Object o2 = JSONUtils.toJavaBean(o1, (Class<?>) returnType);
        return o2;
    }
}
