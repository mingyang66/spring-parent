package com.emily.infrastructure.rpc.client.pool2.pool;

import com.emily.infrastructure.rpc.client.pool.SocketConn;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @program: spring-parent
 * @description: 自定义对象池
 * @author: Emily
 * @create: 2021/09/28
 */
public class RpcObjectPool extends GenericObjectPool<SocketConn> {
    public RpcObjectPool(PooledObjectFactory<SocketConn> factory) {
        super(factory);
    }

    public RpcObjectPool(PooledObjectFactory<SocketConn> factory, GenericObjectPoolConfig<SocketConn> config) {
        super(factory, config);
    }

    public RpcObjectPool(PooledObjectFactory<SocketConn> factory, GenericObjectPoolConfig<SocketConn> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }

    public SocketConn getSocketConn(RpcRequest request) {

        SocketConn conn = null;
        try {
            return this.borrowObject();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (conn != null) {
                this.returnObject(conn);
            }
        }
        return null;
    }
}
