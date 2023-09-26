package com.emily.infrastructure.redis.factory;

import com.emily.infrastructure.common.StringUtils;
import com.emily.infrastructure.core.context.ioc.IOCContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis数据源
 *
 * @author Emily
 * @since 2021/07/11
 */
public class RedisDbFactory {

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
        if (StringUtils.isBlank(key)) {
            return IOCContext.getBean("stringRedisTemplate", StringRedisTemplate.class);
        } else {
            return IOCContext.getBean(key + "StringRedisTemplate", StringRedisTemplate.class);
        }

    }

    /**
     * 获取Redis默认字符串模板
     *
     * @return redis操作对象
     */
    public static RedisTemplate getRedisTemplate() {
        return getRedisTemplate(null);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param key 数据源标识
     * @return redis操作模板对象
     */
    public static RedisTemplate getRedisTemplate(String key) {
        if (StringUtils.isBlank(key)) {
            return IOCContext.getBean("redisTemplate", StringRedisTemplate.class);
        } else {
            return IOCContext.getBean(key + "RedisTemplate", StringRedisTemplate.class);
        }
    }
}
