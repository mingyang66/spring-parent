package com.emily.infrastructure.redis.factory;

import com.emily.infrastructure.common.StringUtils;
import com.emily.infrastructure.core.context.ioc.IocUtils;
import com.emily.infrastructure.redis.common.RedisBeanNames;
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
            return IocUtils.getBean(RedisBeanNames.DEFAULT_STRING_REDIS_TEMPLATE, StringRedisTemplate.class);
        } else {
            return IocUtils.getBean(key + RedisBeanNames.STRING_REDIS_TEMPLATE, StringRedisTemplate.class);
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
            return IocUtils.getBean(RedisBeanNames.DEFAULT_REDIS_TEMPLATE, StringRedisTemplate.class);
        } else {
            return IocUtils.getBean(key + RedisBeanNames.REDIS_TEMPLATE, StringRedisTemplate.class);
        }
    }
}
