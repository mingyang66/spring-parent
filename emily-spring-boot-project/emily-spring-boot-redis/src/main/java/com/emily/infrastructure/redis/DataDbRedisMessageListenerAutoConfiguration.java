package com.emily.infrastructure.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Map;
import java.util.Objects;

import static com.emily.infrastructure.redis.common.DataRedisInfo.*;

/**
 * Redis仓储类
 * {@link org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration}
 *
 * @author :  Emily
 * @since :  2024/7/8 上午19:57
 */
@SuppressWarnings("all")
@AutoConfiguration(after = DataDbRedisAutoConfiguration.class)
@ConditionalOnProperty(prefix = DataDbRedisProperties.PREFIX, name = "listener", havingValue = "true")
public class DataDbRedisMessageListenerAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(DataDbRedisMessageListenerAutoConfiguration.class);
    private final DataDbRedisProperties properties;
    private final DefaultListableBeanFactory beanFactory;

    public DataDbRedisMessageListenerAutoConfiguration(DataDbRedisProperties properties, DefaultListableBeanFactory beanFactory) {
        this.properties = properties;
        this.beanFactory = beanFactory;
    }

    /**
     * 源码初始化位置，{@link RedisKeyValueAdapter#initMessageListenerContainer()} 默认初始化已被默认关闭
     *
     * @param connectionFactory 连接工厂
     * @return 默认消息监听器容器
     */
    @Primary
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "Redis默认标识不可为空");
        RedisMessageListenerContainer redisMessageListenerContainer = null;
        for (Map.Entry<String, RedisProperties> entry : properties.getConfig().entrySet()) {
            String key = entry.getKey();
            // 实例化消息监听容器
            RedisMessageListenerContainer messageListenerContainer = new RedisMessageListenerContainer();
            if (defaultConfig.equals(key)) {
                // 设置连接工厂类
                messageListenerContainer.setConnectionFactory(connectionFactory);
                // 默认容器监听器
                redisMessageListenerContainer = messageListenerContainer;
            } else {
                // 设置连接工厂类
                messageListenerContainer.setConnectionFactory(beanFactory.getBean(join(key, REDIS_CONNECTION_FACTORY), RedisConnectionFactory.class));
                messageListenerContainer.afterPropertiesSet();
                messageListenerContainer.start();
            }
            // 注册redis消息监听容器
            beanFactory.registerSingleton(join(key, REDIS_MESSAGE_LISTENER_CONTAINER), messageListenerContainer);
        }
        return redisMessageListenerContainer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("==> 【初始化--自动化配置】----Redis数据库监听器组件【RedisDbRepositoriesAutoConfiguration】");
    }

    @Override
    public void destroy() throws Exception {
        LOG.info("<== 【销毁--自动化配置】----Redis数据库监听器组件【RedisDbRepositoriesAutoConfiguration】");
    }

}
