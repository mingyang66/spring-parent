package com.emily.infrastructure.redis;

import com.emily.infrastructure.redis.common.RedisInfo;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
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
    private final DefaultListableBeanFactory defaultListableBeanFactory;
    private final RedisDbProperties redisDbProperties;

    public RedisDbReactiveAutoConfiguration(DefaultListableBeanFactory defaultListableBeanFactory, RedisDbProperties redisDbProperties) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        this.redisDbProperties = redisDbProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name = RedisInfo.DEFAULT_REACTIVE_REDIS_TEMPLATE)
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ResourceLoader resourceLoader) {
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(
                resourceLoader.getClassLoader());
        RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext
                .newSerializationContext()
                .key(jdkSerializer)
                .value(jdkSerializer)
                .hashKey(jdkSerializer)
                .hashValue(jdkSerializer)
                .build();
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(this.redisDbProperties.getConfig(), "Redis连接配置不存在");
        ReactiveRedisTemplate reactiveRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            if (redisDbProperties.getDefaultConfig().equals(key)) {
                ReactiveRedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(RedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY, ReactiveRedisConnectionFactory.class);
                reactiveRedisTemplate = new ReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
            } else {
                ReactiveRedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECTION_FACTORY, ReactiveRedisConnectionFactory.class);
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.REACTIVE_REDIS_TEMPLATE, new ReactiveRedisTemplate<>(redisConnectionFactory, serializationContext));
            }
        }
        return reactiveRedisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = RedisInfo.DEFAULT_REACTIVE_STRING_REDIS_TEMPLATE)
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate() {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(this.redisDbProperties.getConfig(), "Redis连接配置不存在");
        ReactiveStringRedisTemplate reactiveStringRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            if (redisDbProperties.getDefaultConfig().equals(key)) {
                ReactiveRedisConnectionFactory reactiveRedisConnectionFactory = defaultListableBeanFactory.getBean(RedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY, ReactiveRedisConnectionFactory.class);
                reactiveStringRedisTemplate = new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
            } else {
                ReactiveRedisConnectionFactory reactiveRedisConnectionFactory = defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECTION_FACTORY, ReactiveRedisConnectionFactory.class);
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.REACTIVE_STRING_REDIS_TEMPLATE, new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory));
            }
        }
        return reactiveStringRedisTemplate;
    }
}
