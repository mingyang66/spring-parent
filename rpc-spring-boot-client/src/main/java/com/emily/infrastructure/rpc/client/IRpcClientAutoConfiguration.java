package com.emily.infrastructure.rpc.client;

import com.emily.infrastructure.rpc.client.pool.IRpcConnection;
import com.emily.infrastructure.rpc.client.pool.IRpcObjectPool;
import com.emily.infrastructure.rpc.client.pool.IRpcPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
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
@EnableConfigurationProperties(IRpcClientProperties.class)
@ConditionalOnProperty(prefix = IRpcClientProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class IRpcClientAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(IRpcClientAutoConfiguration.class);

    private IRpcObjectPool pool;

    @ConditionalOnClass({IRpcPooledObjectFactory.class})
    @Bean
    protected IRpcObjectPool javaObjectPool(IRpcClientProperties properties) {
        IRpcPooledObjectFactory factory = new IRpcPooledObjectFactory(properties);
        //设置对象池的相关参数
        GenericObjectPoolConfig<IRpcConnection> poolConfig = new GenericObjectPoolConfig<>();
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
        pool = new IRpcObjectPool(factory, poolConfig);

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

    @Override
    public void destroy() {
        if (pool != null) {
            pool.close();
        }
        logger.info("<== 【销毁--自动化配置】----Rpc客户端销毁成功【IRpcClientAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----Rpc客户端启动成功【IRpcClientAutoConfiguration】");
    }
}
