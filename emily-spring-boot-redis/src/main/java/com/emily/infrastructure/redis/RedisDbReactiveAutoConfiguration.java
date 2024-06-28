package com.emily.infrastructure.redis;

import com.emily.infrastructure.redis.utils.BeanFactoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
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
public class RedisDbReactiveAutoConfiguration implements InitializingBean, DisposableBean {
    private final RedisDbProperties redisDbProperties;

    public RedisDbReactiveAutoConfiguration(RedisDbProperties redisDbProperties) {
        this.redisDbProperties = redisDbProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name = DEFAULT_REACTIVE_REDIS_TEMPLATE)
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "默认标识不可为空");
        RedisSerializer<Object> javaSerializer = RedisSerializer.java(resourceLoader.getClassLoader());
        RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext
                .newSerializationContext()
                .key(javaSerializer)
                .value(javaSerializer)
                .hashKey(javaSerializer)
                .hashValue(javaSerializer)
                .build();
        ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate = new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            if (defaultConfig.equals(key)) {
                BeanFactoryUtils.registerSingleton(join(key, REACTIVE_REDIS_TEMPLATE), reactiveRedisTemplate);
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
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "默认标识不可为空");
        ReactiveStringRedisTemplate reactiveStringRedisTemplate = new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            if (defaultConfig.equals(key)) {
                BeanFactoryUtils.registerSingleton(join(key, REACTIVE_STRING_REDIS_TEMPLATE), reactiveStringRedisTemplate);
            } else {
                ReactiveRedisConnectionFactory connectionFactory = BeanFactoryUtils.getBean(join(key, REDIS_CONNECTION_FACTORY), ReactiveRedisConnectionFactory.class);
                BeanFactoryUtils.registerSingleton(join(key, REACTIVE_STRING_REDIS_TEMPLATE), new ReactiveStringRedisTemplate(connectionFactory));
            }
        }
        return reactiveStringRedisTemplate;
    }

    @Override
    public void destroy() {
        LogHolder.LOG.info("<== 【销毁--自动化配置】----Redis数据库多数据源组件【RedisDbReactiveAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LogHolder.LOG.info("==> 【初始化--自动化配置】----Redis数据库多数据源组件【RedisDbReactiveAutoConfiguration】");
    }

    private static class LogHolder {
        private static final Logger LOG = LoggerFactory.getLogger(RedisDbReactiveAutoConfiguration.class);
    }
}
