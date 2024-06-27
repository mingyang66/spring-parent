package com.emily.infrastructure.redis.factory;

import com.emily.infrastructure.redis.RedisDbProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import static com.emily.infrastructure.redis.common.RedisBeanNames.*;

/**
 * Redis数据源
 *
 * @author Emily
 * @since 2021/07/11
 */
public class RedisDbFactory {

    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        RedisDbFactory.context = context;
    }

    /**
     * 获取Redis默认字符串模板
     *
     * @return redis操作对象
     */
    public static StringRedisTemplate getStringRedisTemplate() {
        return getStringRedisTemplate(null);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param key 数据源标识
     * @return redis操作对象
     */
    public static StringRedisTemplate getStringRedisTemplate(String key) {
        if (key == null || key.isBlank() || getContext().getBean(RedisDbProperties.class).getDefaultConfig().equals(key)) {
            return getContext().getBean(DEFAULT_STRING_REDIS_TEMPLATE, StringRedisTemplate.class);
        } else {
            return getContext().getBean(join(key, STRING_REDIS_TEMPLATE), StringRedisTemplate.class);
        }
    }

    /**
     * 获取Redis默认字符串模板
     *
     * @return redis操作对象
     */
    public static RedisTemplate<Object, Object> getRedisTemplate() {
        return getRedisTemplate(null);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param key 数据源标识
     * @return redis操作模板对象
     */
    @SuppressWarnings("unchecked")
    public static RedisTemplate<Object, Object> getRedisTemplate(String key) {
        if (key == null || key.isBlank() || getContext().getBean(RedisDbProperties.class).getDefaultConfig().equals(key)) {
            return getContext().getBean(DEFAULT_REDIS_TEMPLATE, RedisTemplate.class);
        } else {
            return getContext().getBean(join(key, REDIS_TEMPLATE), RedisTemplate.class);
        }
    }
}
