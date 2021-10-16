package com.emily.infrastructure.rpc.client.pool;

import com.emily.infrastructure.rpc.client.IRpcClientProperties;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

/**
 * @program: spring-parent
 * @description: 池化工厂类
 * @author: Emily
 * @create: 2021/09/28
 */
public class IRpcPooledObjectFactory implements PooledObjectFactory<IRpcConnection> {

    private Logger logger = LoggerFactory.getLogger(IRpcPooledObjectFactory.class);

    private IRpcClientProperties properties;

    public IRpcPooledObjectFactory(IRpcClientProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建可由对象池服务的实例，并将其包装到PooledObject对象交由池管理
     *
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<IRpcConnection> makeObject() throws Exception {
        logger.info("创建对象...");
        IRpcConnection connection = new IRpcConnection(properties);
        //建立Rpc连接
        connection.connect();
        return new DefaultPooledObject<>(connection);
    }

    /**
     * 销毁对象
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<IRpcConnection> pooledObject) throws Exception {
        logger.info("销毁对象...");
        IRpcConnection connection = pooledObject.getObject();
        if(Objects.nonNull(connection)){
            connection.close();
        }
    }

    /**
     * 激活对象
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<IRpcConnection> pooledObject) throws Exception {
        logger.info("激活对象...");
        IRpcConnection connection = pooledObject.getObject();
        if (!connection.isAvailable()) {
            connection.connect();
        }
    }

    /**
     * 钝化(初始化|归还)一个对象，也可以理解为反初始化
     *
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<IRpcConnection> pooledObject) throws Exception {
        logger.info("钝化对象...");
    }

    /**
     * 验证对象是否可用
     *
     * @param pooledObject
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<IRpcConnection> pooledObject) {
        IRpcConnection connection = pooledObject.getObject();
        if (!connection.isAvailable()) {
            //连接不可用，关闭连接
            connection.close();
            //重新连接
            connection.connect();
        }
        logger.info("验证对象是否可用:{}", connection.isAvailable());
        return connection.isAvailable();
    }

}
