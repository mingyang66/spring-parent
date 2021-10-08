package com.emily.infrastructure.rpc.client.pool;

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
public class RpcObjectPool extends GenericObjectPool<RpcConnection> {
    public RpcObjectPool(PooledObjectFactory<RpcConnection> factory) {
        super(factory);
    }

    public RpcObjectPool(PooledObjectFactory<RpcConnection> factory, GenericObjectPoolConfig<RpcConnection> config) {
        super(factory, config);
    }

    public RpcObjectPool(PooledObjectFactory<RpcConnection> factory, GenericObjectPoolConfig<RpcConnection> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
