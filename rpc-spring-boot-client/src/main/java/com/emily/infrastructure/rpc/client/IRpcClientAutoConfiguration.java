package com.emily.infrastructure.rpc.client;

import com.emily.infrastructure.rpc.client.pool.IRpcConnection;
import com.emily.infrastructure.rpc.client.pool.IRpcObjectPool;
import com.emily.infrastructure.rpc.client.pool.IRpcPooledObjectFactory;
import com.emily.infrastructure.rpc.core.exception.ObjectPoolException;
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
import java.time.Duration;

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

    @Bean
    @ConditionalOnClass({IRpcPooledObjectFactory.class})
    protected IRpcObjectPool javaObjectPool(IRpcClientProperties properties) {
        IRpcPooledObjectFactory factory = new IRpcPooledObjectFactory(properties);
        //设置对象池的相关参数
        GenericObjectPoolConfig<IRpcConnection> poolConfig = new GenericObjectPoolConfig<>();
        //最大空闲连接数
        poolConfig.setMaxIdle(properties.getPool().getMaxIdle());
        //最小空闲连接数
        poolConfig.setMinIdle(properties.getPool().getMinIdle());
        //最大链接数
        poolConfig.setMaxTotal(properties.getPool().getMaxTotal());
        //当对象池没有空闲对象时，新的获取对象的请求是否阻塞，true-阻塞(maxWait才生效)
        poolConfig.setBlockWhenExhausted(true);
        //对象池中无对象时最大等待时间
        poolConfig.setMaxWaitMillis(100);
        //向调用者输出"链接"资源时，是否检测有效性，如果无效则从连接池中移除，并继续尝试获取，默认：false
        poolConfig.setTestOnBorrow(true);
        //向链接池归还链接时，是否检测链接对象的有效性，默认：false
        poolConfig.setTestOnReturn(true);
        //向调用者输出链接对象时，是否检测它的空闲超时，默认：false
        poolConfig.setTestWhileIdle(true);
        //空闲链接检测线程，检测周期，单位：毫秒，如果为负值，标识不运行检测线程，默认：-1
        poolConfig.setTimeBetweenEvictionRunsMillis(60*1000);
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
                throw new ObjectPoolException();
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
