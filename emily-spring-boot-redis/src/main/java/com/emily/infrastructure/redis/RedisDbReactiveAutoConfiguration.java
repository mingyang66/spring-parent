package com.emily.infrastructure.redis;

import com.emily.infrastructure.redis.utils.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;

import static com.emily.infrastructure.redis.common.RedisBeanNames.*;

/**
 * @author :  Emily
 * @since :  2023/9/25 21:51 PM
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration(after = {RedisDbAutoConfiguration.class})
@ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisTemplate.class, Flux.class})
public class RedisDbReactiveAutoConfiguration {
    private final RedisDbProperties redisDbProperties;

    public RedisDbReactiveAutoConfiguration(RedisDbProperties redisDbProperties) {
        this.redisDbProperties = redisDbProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name = DEFAULT_REACTIVE_REDIS_TEMPLATE)
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {
        RedisSerializer<Object> javaSerializer = RedisSerializer.java(resourceLoader.getClassLoader());
        RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext
                .newSerializationContext()
                .key(javaSerializer)
                .value(javaSerializer)
                .hashKey(javaSerializer)
                .hashValue(javaSerializer)
                .build();
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "默认标识不可为空");
        ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            if (defaultConfig.equals(key)) {
                reactiveRedisTemplate = new ReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
            } else {
                ReactiveRedisConnectionFactory connectionFactory = BeanFactoryUtils.getBean(join(key, REDIS_CONNECTION_FACTORY), ReactiveRedisConnectionFactory.class);
                BeanFactoryUtils.registerSingleton(join(key, REACTIVE_REDIS_TEMPLATE), new ReactiveRedisTemplate<>(connectionFactory, serializationContext));
            }
        }
        return reactiveRedisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = DEFAULT_REACTIVE_STRING_REDIS_TEMPLATE)
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory redisConnectionFactory) {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "默认标识不可为空");
        ReactiveStringRedisTemplate reactiveStringRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            if (defaultConfig.equals(key)) {
                reactiveStringRedisTemplate = new ReactiveStringRedisTemplate(redisConnectionFactory);
            } else {
                ReactiveRedisConnectionFactory connectionFactory = BeanFactoryUtils.getBean(join(key, REDIS_CONNECTION_FACTORY), ReactiveRedisConnectionFactory.class);
                BeanFactoryUtils.registerSingleton(join(key, REACTIVE_STRING_REDIS_TEMPLATE), new ReactiveStringRedisTemplate(connectionFactory));
            }
        }
        return reactiveStringRedisTemplate;
    }
}
