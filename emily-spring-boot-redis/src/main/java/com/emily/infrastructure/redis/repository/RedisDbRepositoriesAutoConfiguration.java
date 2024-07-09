package com.emily.infrastructure.redis.repository;

import com.emily.infrastructure.redis.RedisDbAutoConfiguration;
import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.RedisProperties;
import com.emily.infrastructure.redis.factory.BeanFactoryProvider;
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
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.repository.support.RedisRepositoryFactoryBean;

import java.util.Map;
import java.util.Objects;

/**
 * Redis仓储类
 *
 * @author :  Emily
 * @since :  2024/7/8 上午19:57
 */
@AutoConfiguration(after = RedisDbAutoConfiguration.class)
@ConditionalOnClass(EnableRedisRepositories.class)
@ConditionalOnBean(RedisConnectionFactory.class)
@ConditionalOnProperty(prefix = "spring.emily.redis.repositories", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(RedisRepositoryFactoryBean.class)
@Import(RedisDbRepositoriesRegistrar.class)
public class RedisDbRepositoriesAutoConfiguration implements InitializingBean {

    @Bean
    @Primary
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisDbProperties redisDbProperties = BeanFactoryProvider.getBean(RedisDbProperties.class);
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "Redis默认标识不可为空");
        RedisMessageListenerContainer redisMessageListenerContainer = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            RedisMessageListenerContainer messageListenerContainer = new RedisMessageListenerContainer();
            if (defaultConfig.equals(key)) {
                messageListenerContainer.setConnectionFactory(connectionFactory);
                redisMessageListenerContainer = messageListenerContainer;
            } else {
                messageListenerContainer.setConnectionFactory(BeanFactoryProvider.getBean(key + "RedisConnectionFactory", RedisConnectionFactory.class));
                messageListenerContainer.afterPropertiesSet();
                messageListenerContainer.start();
            }
            BeanFactoryProvider.registerSingleton(key + "RedisMessageListenerContainer", messageListenerContainer);
        }
        return redisMessageListenerContainer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
