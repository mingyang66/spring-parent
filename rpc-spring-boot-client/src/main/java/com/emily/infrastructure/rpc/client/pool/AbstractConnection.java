package com.emily.infrastructure.rpc.client.pool;

/**
 * @program: spring-parent
 * @description: TCP客户端连接基础类
 * @author: Emily
 * @create: 2021/09/24
 */
public abstract class AbstractConnection<T> {
    /**
     * 客户端连接对象
     */
    private T connection;

    /**
     * 创建连接
     *
     * @return
     */
    public abstract boolean connect();

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

    public T getConnection() {
        return connection;
    }

    public void setConnection(T connection) {
        this.connection = connection;
    }
}
