package com.emily.infrastructure.rpc.client.pool;

import com.emily.infrastructure.rpc.client.RpcClientProperties;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @program: spring-parent
 * @description: 池化工厂类
 * @author: Emily
 * @create: 2021/09/28
 */
public class RpcPooledObjectFactory implements PooledObjectFactory<RpcConnection> {

    private Logger logger = LoggerFactory.getLogger(RpcPooledObjectFactory.class);

    private RpcClientProperties properties;

    public RpcPooledObjectFactory(RpcClientProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建可由对象池服务的实例，并将其包装到PooledObject对象交由池管理
     *
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<RpcConnection> makeObject() throws Exception {
        logger.info("创建对象...");
        return new DefaultPooledObject<>(new RpcConnection(properties));
    }

    /**
     * 销毁对象
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<RpcConnection> pooledObject) throws Exception {
        logger.info("销毁对象...");
        RpcConnection conn = pooledObject.getObject();
        Optional.ofNullable(conn).ifPresent(RpcConnection::close);
    }

    /**
     * 激活对象
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<RpcConnection> pooledObject) throws Exception {
        logger.info("激活对象...");
        pooledObject.getObject().connect();
    }

    /**
     * 钝化一个对象，也可以理解为反初始化
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<RpcConnection> pooledObject) throws Exception {
        logger.info("钝化对象...");
        RpcConnection testObject = pooledObject.getObject();
    }

    /**
     * 验证对象是否可用
     *
     * @param pooledObject
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<RpcConnection> pooledObject) {
        logger.info("验证对象是否可用...");
        return pooledObject.getObject().isAvailable();
    }

}
