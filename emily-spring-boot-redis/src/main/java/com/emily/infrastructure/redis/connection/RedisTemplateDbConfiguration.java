package com.emily.infrastructure.redis.connection;

import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.common.RedisInfo;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Objects;

/**
 * @author :  Emily
 * @since :  2023/9/25 9:25 PM
 */
@Configuration(proxyBeanMethods = false)
public class RedisTemplateDbConfiguration {
    private final DefaultListableBeanFactory defaultListableBeanFactory;
    private final RedisDbProperties redisDbProperties;

    public RedisTemplateDbConfiguration(DefaultListableBeanFactory defaultListableBeanFactory, RedisDbProperties redisDbProperties) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        this.redisDbProperties = redisDbProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name = RedisInfo.DEFAULT_REDIS_TEMPLATE)
    public RedisTemplate<Object, Object> redisTemplate() {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(redisDbProperties.getConfig(), "Redis连接配置不存在");
        RedisTemplate<Object, Object> redisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            if (redisDbProperties.getDefaultConfig().equals(key)) {
                RedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(RedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class);
                template.setConnectionFactory(redisConnectionFactory);
                redisTemplate = template;
            } else {
                RedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class);
                template.setConnectionFactory(redisConnectionFactory);
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.REDIS_TEMPLATE, template);
            }
        }

        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate() {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(redisDbProperties.getConfig(), "Redis连接配置不存在");
        StringRedisTemplate stringRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            StringRedisTemplate template = new StringRedisTemplate();
            if (redisDbProperties.getDefaultConfig().equals(key)) {
                RedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(RedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class);
                template.setConnectionFactory(redisConnectionFactory);
                stringRedisTemplate = template;
            } else {
                RedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class);
                template.setConnectionFactory(redisConnectionFactory);
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.STRING_REDIS_TEMPLATE, template);
            }
        }

        return stringRedisTemplate;
    }
}
