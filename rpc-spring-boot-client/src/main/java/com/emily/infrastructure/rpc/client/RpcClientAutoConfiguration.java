package com.emily.infrastructure.rpc.client;

import com.emily.infrastructure.rpc.client.pool.RpcConnection;
import com.emily.infrastructure.rpc.client.pool.RpcPooledObjectFactory;
import com.emily.infrastructure.rpc.client.pool.RpcObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import javax.annotation.PreDestroy;

/**
 * @program: spring-parent
 * @description: RPC客户端代理配置类
 * @author: Emily
 * @create: 2021/09/22
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
@ConditionalOnProperty(prefix = RpcClientProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RpcClientAutoConfiguration {

    private RpcObjectPool pool;

    @ConditionalOnClass({RpcPooledObjectFactory.class})
    @Bean
    protected RpcObjectPool javaObjectPool(RpcClientProperties properties) {
        RpcPooledObjectFactory faceSDKFactory = new RpcPooledObjectFactory(properties);
        //设置对象池的相关参数
        GenericObjectPoolConfig<RpcConnection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(properties.getPool().getMaxIdle());
        poolConfig.setMaxTotal(properties.getPool().getMaxTotal());
        poolConfig.setMinIdle(properties.getPool().getMinIdle());
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(1000 * 60 * 30);
        //一定要关闭jmx，不然springboot启动会报已经注册了某个jmx的错误
        poolConfig.setJmxEnabled(false);

        //新建一个对象池,传入对象工厂和配置
        pool = new RpcObjectPool(faceSDKFactory, poolConfig);

        initPool(properties.getPool().getInitialSize(), properties.getPool().getMaxIdle());
        return pool;
    }

    /**
     * 预先加载testObject对象到对象池中
     *
     * @param initialSize 初始化连接数
     * @param maxIdle     最大空闲连接数
     */
    private void initPool(int initialSize, int maxIdle) {
        if (initialSize <= 0) {
            return;
        }

        int size = Math.min(initialSize, maxIdle);
        for (int i = 0; i < size; i++) {
            try {
                pool.addObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if (pool != null) {
            pool.close();
        }
    }

}
