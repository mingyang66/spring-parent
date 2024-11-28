package com.emily.infrastructure.redis.repository.data;

import com.emily.infrastructure.redis.RedisDbAutoConfiguration;
import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.RedisProperties;
import com.emily.infrastructure.redis.factory.BeanFactoryProvider;
import com.emily.infrastructure.redis.repository.EnableRedisDbRepositories;
import com.emily.infrastructure.redis.repository.RedisDbKeyValueAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
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

import static com.emily.infrastructure.redis.common.RedisBeanNames.*;

/**
 * Redis仓储类
 * {@link org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration}
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
public class RedisDbRepositoriesAutoConfiguration implements InitializingBean, DisposableBean {
    private final RedisDbProperties redisDbProperties;

    RedisDbRepositoriesAutoConfiguration(RedisDbProperties redisDbProperties) {
        this.redisDbProperties = redisDbProperties;
    }

    private static KeyExpirationEventMessageListener getKeyExpirationEventMessageListener(RedisMessageListenerContainer redisMessageListenerContainer, RedisOperations<?, ?> redisOps, RedisDbKeyValueAdapter redisKeyValueAdapter, RedisProperties properties) {
        RedisDbKeyValueAdapter.MappingExpirationListener listener = new RedisDbKeyValueAdapter.MappingExpirationListener(redisMessageListenerContainer, redisOps, redisKeyValueAdapter.getConverter(), RedisDbKeyValueAdapter.ShadowCopy.DEFAULT);
        if (redisKeyValueAdapter.getKeyspaceNotificationsConfigParameter() != null) {
            listener.setKeyspaceNotificationsConfigParameter(redisKeyValueAdapter.getKeyspaceNotificationsConfigParameter());
        }
        if (redisKeyValueAdapter.getEventPublisher() != null) {
            listener.setApplicationEventPublisher(redisKeyValueAdapter.getEventPublisher());
        }
        listener.setRedisProperties(properties);
        listener.init();
        return listener;
    }

    @Primary
    @Bean
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
                messageListenerContainer.setConnectionFactory(BeanFactoryProvider.getBean(join(key, REDIS_CONNECTION_FACTORY), RedisConnectionFactory.class));
                messageListenerContainer.afterPropertiesSet();
                messageListenerContainer.start();
            }
            // 注册redis消息监听容器
            BeanFactoryProvider.registerSingleton(join(key, REDIS_MESSAGE_LISTENER_CONTAINER), messageListenerContainer);
        }
        return redisMessageListenerContainer;
    }

    @Bean
    public KeyExpirationEventMessageListener keyExpirationEventMessageListener(RedisMessageListenerContainer messageListenerContainer) {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "Redis默认标识不可为空");
        RedisDbKeyValueAdapter redisKeyValueAdapter = BeanFactoryProvider.getBean(RedisDbKeyValueAdapter.class);
        KeyExpirationEventMessageListener keyExpirationListener = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            RedisProperties properties = entry.getValue();
            // 获取Redis异步消息监听容器
            RedisMessageListenerContainer redisMessageListenerContainer = defaultConfig.equals(key) ? messageListenerContainer : BeanFactoryProvider.getBean(join(key, REDIS_MESSAGE_LISTENER_CONTAINER), RedisMessageListenerContainer.class);
            // 获取Redis操作对象
            RedisOperations<?, ?> redisOps = BeanFactoryProvider.getBean(join(key, REDIS_TEMPLATE), RedisTemplate.class);
            // 获取Redis key过期事件监听器
            KeyExpirationEventMessageListener listener = getKeyExpirationEventMessageListener(redisMessageListenerContainer, redisOps, redisKeyValueAdapter, properties);
            // 监听器注入容器
            BeanFactoryProvider.registerSingleton(join(key, KEY_EXPIRATION_EVENT_MESSAGE_LISTENER), listener);
            if (defaultConfig.equals(key)) {
                // 默认监听器
                keyExpirationListener = listener;
                // 启动的时候开启keyExpirationListener开关
                redisKeyValueAdapter.setEnableKeyspaceEvents(RedisDbKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP);
                // 初始化过期监听器
                redisKeyValueAdapter.getExpirationListener().compareAndSet(null, keyExpirationListener);
            }
        }
        return keyExpirationListener;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogHolder.LOG.info("==> 【初始化--自动化配置】----Redis数据库监听器组件【RedisDbRepositoriesAutoConfiguration】");
    }

    @Override
    public void destroy() throws Exception {
        LogHolder.LOG.info("<== 【销毁--自动化配置】----Redis数据库监听器组件【RedisDbRepositoriesAutoConfiguration】");
    }

    static class LogHolder {
        private static final Logger LOG = LoggerFactory.getLogger(RedisDbRepositoriesAutoConfiguration.class);
    }
}
