package com.emily.infrastructure.redis.repository.data;

import com.emily.infrastructure.redis.RedisDbAutoConfiguration;
import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.RedisProperties;
import com.emily.infrastructure.redis.factory.BeanFactoryProvider;
import com.emily.infrastructure.redis.repository.EnableRedisDbRepositories;
import com.emily.infrastructure.redis.repository.RedisDbKeyValueAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.support.RedisRepositoryFactoryBean;

import java.util.Map;
import java.util.Objects;

/**
 * Redis仓储类
 *
 * @author :  Emily
 * @since :  2024/7/8 上午19:57
 */
@SuppressWarnings("all")
@AutoConfiguration(after = RedisDbAutoConfiguration.class)
@ConditionalOnClass(EnableRedisDbRepositories.class)
@ConditionalOnBean(RedisConnectionFactory.class)
@ConditionalOnProperty(prefix = RedisDbProperties.PREFIX, name = "listener", havingValue = "true")
@ConditionalOnMissingBean(RedisRepositoryFactoryBean.class)
@Import(RedisDbRepositoriesRegistrar.class)
public class RedisDbRepositoriesAutoConfiguration implements InitializingBean {
    private static final String REDIS_CONVERTER_BEAN_NAME = "redisConverter";
    private static final String REDIS_ADAPTER_BEAN_NAME = "redisKeyValueAdapter";
    private final RedisDbProperties redisDbProperties;

    RedisDbRepositoriesAutoConfiguration(RedisDbProperties redisDbProperties) {
        this.redisDbProperties = redisDbProperties;
    }

    @Primary
    @Bean(destroyMethod = "destroy")
    public RedisMessageListenerContainer messageListenerContainer(RedisConnectionFactory connectionFactory) {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "Redis默认标识不可为空");
        RedisDbKeyValueAdapter redisKeyValueAdapter = BeanFactoryProvider.getBean(RedisDbKeyValueAdapter.class);
        RedisMessageListenerContainer redisMessageListenerContainer = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            // 实例化消息监听容器
            RedisMessageListenerContainer messageListenerContainer = new RedisMessageListenerContainer();
            if (defaultConfig.equals(key)) {
                // 设置连接工厂类
                messageListenerContainer.setConnectionFactory(connectionFactory);
                // 默认容器监听器
                redisMessageListenerContainer = messageListenerContainer;
                // 设置默认消息监听容器
                redisKeyValueAdapter.setMessageListenerContainer(redisMessageListenerContainer);
            } else {
                // 设置连接工厂类
                messageListenerContainer.setConnectionFactory(BeanFactoryProvider.getBean(key + "RedisConnectionFactory", RedisConnectionFactory.class));
                messageListenerContainer.afterPropertiesSet();
                messageListenerContainer.start();
            }
            // 注册redis消息监听容器
            BeanFactoryProvider.registerSingleton(key + "RedisMessageListenerContainer", messageListenerContainer);
        }
        return redisMessageListenerContainer;
    }


    @Bean(destroyMethod = "destroy")
    public KeyExpirationEventMessageListener keyExpirationEventMessageListener(RedisMessageListenerContainer messageListenerContainer) {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "Redis默认标识不可为空");
        RedisDbKeyValueAdapter redisKeyValueAdapter = BeanFactoryProvider.getBean(RedisDbKeyValueAdapter.class);
        KeyExpirationEventMessageListener keyExpirationListener = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            // 获取Redis异步消息监听容器
            RedisMessageListenerContainer redisMessageListenerContainer = defaultConfig.equals(key) ? messageListenerContainer : BeanFactoryProvider.getBean(key + "RedisMessageListenerContainer", RedisMessageListenerContainer.class);
            // 获取Redis操作对象
            RedisOperations<?, ?> redisOps = BeanFactoryProvider.getBean(key + "RedisTemplate", RedisTemplate.class);
            // 获取Redis key过期事件监听器
            KeyExpirationEventMessageListener listener = getKeyExpirationEventMessageListener(redisMessageListenerContainer, redisOps, redisKeyValueAdapter);
            // 监听器注入容器
            BeanFactoryProvider.registerSingleton(key + "KeyExpirationEventMessageListener", listener);
            if (defaultConfig.equals(key)) {
                // 默认监听器
                keyExpirationListener = listener;
                // 初始化过期监听器
                redisKeyValueAdapter.getExpirationListener().compareAndSet(null, keyExpirationListener);
            }
        }
        return keyExpirationListener;
    }

    private static KeyExpirationEventMessageListener getKeyExpirationEventMessageListener(RedisMessageListenerContainer redisMessageListenerContainer, RedisOperations<?, ?> redisOps, RedisDbKeyValueAdapter redisKeyValueAdapter) {
        KeyExpirationEventMessageListener listener = new RedisDbKeyValueAdapter.MappingExpirationListener(redisMessageListenerContainer, redisOps, redisKeyValueAdapter.getConverter());
        if (redisKeyValueAdapter.getKeyspaceNotificationsConfigParameter() != null) {
            listener.setKeyspaceNotificationsConfigParameter(redisKeyValueAdapter.getKeyspaceNotificationsConfigParameter());
        }
        if (redisKeyValueAdapter.getEventPublisher() != null) {
            listener.setApplicationEventPublisher(redisKeyValueAdapter.getEventPublisher());
        }
        listener.init();
        return listener;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
