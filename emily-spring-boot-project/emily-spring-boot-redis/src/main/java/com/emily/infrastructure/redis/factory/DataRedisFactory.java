package com.emily.infrastructure.redis.factory;

import com.emily.infrastructure.redis.common.DataRedisInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis数据源
 *
 * @author Emily
 * @since 2021/07/11
 */
public class DataRedisFactory {

    private static ApplicationContext context;

    public static void registerApplicationContext(ApplicationContext context) {
        DataRedisFactory.context = context;
    }

    /**
     * 获取Redis默认字符串模板
     *
     * @return redis操作对象
     */
    public static StringRedisTemplate getStringRedisTemplate() {
        return context.getBean(DataRedisInfo.DEFAULT_STRING_REDIS_TEMPLATE, StringRedisTemplate.class);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param key 数据源标识
     * @return redis操作对象
     */
    public static StringRedisTemplate getStringRedisTemplate(String key) {
        if (key == null || key.isBlank()) {
            return getStringRedisTemplate();
        }
        return context.getBean(StringUtils.join(key, DataRedisInfo.STRING_REDIS_TEMPLATE), StringRedisTemplate.class);
    }

    /**
     * 获取Redis默认字符串模板
     *
     * @return redis操作对象
     */
    @SuppressWarnings("unchecked")
    public static RedisTemplate<Object, Object> getRedisTemplate() {
        return context.getBean(DataRedisInfo.DEFAULT_REDIS_TEMPLATE, RedisTemplate.class);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param key 数据源标识
     * @return redis操作模板对象
     */
    @SuppressWarnings("unchecked")
    public static RedisTemplate<Object, Object> getRedisTemplate(String key) {
        if (key == null || key.isBlank()) {
            return getRedisTemplate();
        } else {
            return context.getBean(StringUtils.join(key, DataRedisInfo.REDIS_TEMPLATE), RedisTemplate.class);
        }
    }

    /**
     * 获取响应式Redis模板对象
     *
     * @return redis响应是模板对象
     */
    @SuppressWarnings("unchecked")
    public static ReactiveRedisTemplate<Object, Object> getReactiveRedisTemplate() {
        return context.getBean(DataRedisInfo.DEFAULT_REACTIVE_REDIS_TEMPLATE, ReactiveRedisTemplate.class);
    }

    /**
     * 获取响应式Redis模板对象
     *
     * @param key 数据库标识
     * @return redis响应是模板对象
     */
    @SuppressWarnings("unchecked")
    public static ReactiveRedisTemplate<Object, Object> getReactiveRedisTemplate(String key) {
        if (key == null || key.isBlank()) {
            return getReactiveRedisTemplate();
        }
        return context.getBean(StringUtils.join(key, DataRedisInfo.REACTIVE_REDIS_TEMPLATE), ReactiveRedisTemplate.class);
    }

    /**
     * 获取响应式Redis模板对象
     *
     * @return redis响应是模板对象
     */
    public static ReactiveStringRedisTemplate getReactiveStringRedisTemplate() {
        return context.getBean(DataRedisInfo.DEFAULT_REACTIVE_STRING_REDIS_TEMPLATE, ReactiveStringRedisTemplate.class);
    }

    /**
     * 获取响应式Redis模板对象
     *
     * @param key 数据库标识
     * @return redis响应是模板对象
     */
    public static ReactiveStringRedisTemplate getReactiveStringRedisTemplate(String key) {
        if (key == null || key.isBlank()) {
            return getReactiveStringRedisTemplate();
        }
        return context.getBean(StringUtils.join(key, DataRedisInfo.REACTIVE_STRING_REDIS_TEMPLATE), ReactiveStringRedisTemplate.class);
    }
}
