package com.emily.infrastructure.rpc.client.pool;

import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;

/**
 * @program: spring-parent
 * @description: TCP客户端连接基础类
 * @author: Emily
 * @create: 2021/09/24
 */
public abstract class RpcConnection<T> {
    /**
     * 客户端连接对象
     */
    public T connection;

    /**
     * 创建连接
     *
     * @return
     */
    public abstract boolean connect();

    /**
     * 发送请求
     *
     * @param request 请求参数
     * @return
     */
    public abstract RpcResponse call(RpcRequest request);

    /**
     * 通道是否可用
     *
     * @return
     */
    public abstract boolean isAvailable();

    /**
     * 关闭通道连接
     */
    public abstract void close();
}
