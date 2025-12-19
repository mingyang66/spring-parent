package com.emily.infrastructure.redis;

import com.emily.infrastructure.redis.common.DataRedisInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
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

/**
 * org.springframework.boot.data.redis.autoconfigure.DataRedisReactiveAutoConfiguration
 *
 * @author :  Emily
 * @since :  2023/9/25 21:51 PM
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration(after = {DataDbRedisAutoConfiguration.class})
@ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisTemplate.class, Flux.class})
public class DataDbRedisReactiveAutoConfiguration implements InitializingBean, DisposableBean {
    private final DataDbRedisProperties properties;
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public DataDbRedisReactiveAutoConfiguration(DataDbRedisProperties properties, DefaultListableBeanFactory defaultListableBeanFactory) {
        this.properties = properties;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(name = DataRedisInfo.DEFAULT_REACTIVE_REDIS_TEMPLATE)
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(ResourceLoader resourceLoader) {
        RedisSerializer<Object> javaSerializer = RedisSerializer.java(resourceLoader.getClassLoader());
        RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext
                .newSerializationContext()
                .key(javaSerializer)
                .value(RedisSerializer.json())
                .hashKey(RedisSerializer.string())
                .hashValue(RedisSerializer.json())
                .build();
        ReactiveRedisTemplate<Object, Object> redisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : properties.getConfig().entrySet()) {
            ReactiveRedisConnectionFactory factory = defaultListableBeanFactory.getBean(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_CONNECTION_FACTORY), ReactiveRedisConnectionFactory.class);
            ReactiveRedisTemplate<Object, Object> template = new ReactiveRedisTemplate<>(factory, serializationContext);
            defaultListableBeanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRedisInfo.REACTIVE_REDIS_TEMPLATE), template);
            if (properties.getDefaultConfig().equals(entry.getKey())) {
                redisTemplate = template;
            }
        }
        return redisTemplate;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(name = DataRedisInfo.DEFAULT_REACTIVE_STRING_REDIS_TEMPLATE)
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate() {
        for (Map.Entry<String, RedisProperties> entry : properties.getConfig().entrySet()) {
            ReactiveRedisConnectionFactory connectionFactory = defaultListableBeanFactory.getBean(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_CONNECTION_FACTORY), ReactiveRedisConnectionFactory.class);
            defaultListableBeanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRedisInfo.REACTIVE_STRING_REDIS_TEMPLATE), new ReactiveStringRedisTemplate(connectionFactory));
        }
        return defaultListableBeanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRedisInfo.REACTIVE_STRING_REDIS_TEMPLATE), ReactiveStringRedisTemplate.class);
    }

    @Override
    public void destroy() {
        LogHolder.LOG.info("<== 【销毁--自动化配置】----Redis数据库多数据源组件【DataDbRedisReactiveAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LogHolder.LOG.info("==> 【初始化--自动化配置】----Redis数据库多数据源组件【DataDbRedisReactiveAutoConfiguration】");
    }

    private static class LogHolder {
        private static final Logger LOG = LoggerFactory.getLogger(DataDbRedisReactiveAutoConfiguration.class);
    }
}
