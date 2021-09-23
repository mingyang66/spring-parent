package com.emily.infrastructure.rpc.core.client.proxy;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.rpc.core.client.handler.RpcClientChannelHandler;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;

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
public class RpcMethodProxy implements InvocationHandler {
    private RpcClientChannelHandler rpcClientChannelHandler;
    private String className;

    public RpcMethodProxy(RpcClientChannelHandler rpcClientChannelHandler, String className) {
        this.rpcClientChannelHandler = rpcClientChannelHandler;
        this.className = className;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //组装传输类的属性值
        RpcRequest rpcRequest = new RpcRequest(className, method.getName(), method.getParameterTypes(), args);
        rpcClientChannelHandler.setRpcRequest(rpcRequest);
        //运行线程，发送数据
        Future future = ThreadPoolHelper.threadPoolTaskExecutor().submit(rpcClientChannelHandler);
        //返回结果
        RpcResponse rpcResponse = (RpcResponse) future.get();
        //获取返回类型，并将服务端返回的json数据转化为对应的类型
        Type returnType = method.getAnnotatedReturnType().getType();
        //Object o2 = JSONUtils.toJavaBean(rpcResponse.getData(), (Class<?>) returnType);
        if (rpcResponse.getData() instanceof String) {
            return rpcResponse.getData();
        }
        return JSONUtils.toJavaBean(JSONUtils.toJSONString(rpcResponse.getData()), (Class<?>) returnType);
    }
}
