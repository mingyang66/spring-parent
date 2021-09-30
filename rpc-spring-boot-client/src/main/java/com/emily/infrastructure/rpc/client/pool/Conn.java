package com.emily.infrastructure.rpc.client.pool;

import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;

/**
 * @program: spring-parent
 * @description: TCP客户端连接基础类
 * @author: Emily
 * @create: 2021/09/24
 */
public abstract class Conn<T> {
    public T conn;

    /**
     * 创建连接
     */
    public abstract boolean connection();

    /**
     * 发送请求
     */
    public abstract RpcResponse sendRequest(RpcRequest request);
}
