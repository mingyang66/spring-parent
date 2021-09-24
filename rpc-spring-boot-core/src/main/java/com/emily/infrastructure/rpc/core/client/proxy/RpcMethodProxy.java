package com.emily.infrastructure.rpc.core.client.proxy;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.core.ioc.IOCContext;
import com.emily.infrastructure.rpc.core.client.RpcClient;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @program: spring-parent
 * @description: RPC客户端动态代理调用处理程序
 * @author: Emily
 * @create: 2021/09/22
 */
public class RpcMethodProxy implements InvocationHandler {
    private String className;

    public RpcMethodProxy(String className) {
        this.className = className;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //组装传输类的属性值
        RpcRequest rpcRequest = new RpcRequest(className, method.getName(), method.getParameterTypes(), args);
        //运行线程，发送数据
        RpcResponse response = IOCContext.getBean(RpcClient.class).sendRequest(rpcRequest);
        //获取返回类型，并将服务端返回的json数据转化为对应的类型
        Type returnType = method.getAnnotatedReturnType().getType();
        System.out.println(response.getData());
        if (response.getData() instanceof String) {
            return response.getData();
        }
        return JSONUtils.toJavaBean(JSONUtils.toJSONString(response.getData()), (Class<?>) returnType);
    }
}
