package com.emily.infrastructure.redis;

import com.emily.infrastructure.core.context.ioc.BeanFactoryUtils;
import com.emily.infrastructure.redis.common.RedisBeanNames;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;

/**
 * @author :  Emily
 * @since :  2023/9/25 21:51 PM
 */
@AutoConfiguration(after = RedisDbAutoConfiguration.class, before = RedisReactiveAutoConfiguration.class)
@ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisTemplate.class, Flux.class})
public class RedisDbReactiveAutoConfiguration {
    private final RedisDbProperties redisDbProperties;

    public RedisDbReactiveAutoConfiguration(RedisDbProperties redisDbProperties) {
        this.redisDbProperties = redisDbProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name = RedisBeanNames.DEFAULT_REACTIVE_REDIS_TEMPLATE)
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(
                resourceLoader.getClassLoader());
        RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext
                .newSerializationContext()
                .key(jdkSerializer)
                .value(jdkSerializer)
                .hashKey(jdkSerializer)
                .hashValue(jdkSerializer)
                .build();
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "默认标识不可为空");
        ReactiveRedisTemplate reactiveRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            if (defaultConfig.equals(key)) {
                reactiveRedisTemplate = new ReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
            } else {
                ReactiveRedisConnectionFactory connectionFactory = BeanFactoryUtils.getBean(key + RedisBeanNames.REDIS_CONNECTION_FACTORY, ReactiveRedisConnectionFactory.class);
                BeanFactoryUtils.registerSingleton(key + RedisBeanNames.REACTIVE_REDIS_TEMPLATE, new ReactiveRedisTemplate<>(connectionFactory, serializationContext));
            }
        }
        return reactiveRedisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = RedisBeanNames.DEFAULT_REACTIVE_STRING_REDIS_TEMPLATE)
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory redisConnectionFactory) {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "默认标识不可为空");
        ReactiveStringRedisTemplate reactiveStringRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            if (defaultConfig.equals(key)) {
                reactiveStringRedisTemplate = new ReactiveStringRedisTemplate(redisConnectionFactory);
            } else {
                ReactiveRedisConnectionFactory connectionFactory = BeanFactoryUtils.getBean(key + RedisBeanNames.REDIS_CONNECTION_FACTORY, ReactiveRedisConnectionFactory.class);
                BeanFactoryUtils.registerSingleton(key + RedisBeanNames.REACTIVE_STRING_REDIS_TEMPLATE, new ReactiveStringRedisTemplate(connectionFactory));
            }
        }
        return reactiveStringRedisTemplate;
    }
}
