package com.emily.infrastructure.redis.factory;

import com.emily.infrastructure.core.context.ioc.IOCContext;
import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.exception.RedisDbNotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: spring-parent
 * @description: Redis数据源
 * @author: Emily
 * @create: 2021/07/11
 */
public class RedisDbFactory {
    /**
     * 字符串前缀
     */
    private static final String PREFIX_STRING = "ST";
    /**
     * RestTemplate对象前缀
     */
    private static final String PREFIX_REST = "RT";
    /**
     * StringRedisTemplate对象缓存
     */
    private static final Map<String, StringRedisTemplate> stringCache = new ConcurrentHashMap<>();
    /**
     * RedisTemplate对象缓存
     */
    private static final Map<String, RedisTemplate> restCache = new ConcurrentHashMap<>();

    /**
     * 获取Redis默认字符串模板
     *
     * @return
     */
    public static StringRedisTemplate getStringRedisTemplate() {
        return getStringRedisTemplate(null);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param redisMark 数据源标识
     * @return
     */
    public static StringRedisTemplate getStringRedisTemplate(String redisMark) {
        if (Objects.isNull(redisMark)) {
            redisMark = IOCContext.getBean(RedisDbProperties.class).getDefaultConfig();
        }
        /**
         * 获取缓存key
         */
        String key = getStringRedisTemplateBeanName(redisMark);
        if (stringCache.containsKey(key)) {
            return stringCache.get(key);
        }
        if (!IOCContext.containsBean(key)) {
            throw new RedisDbNotFoundException(MessageFormat.format("Redis数据库标识【{0}】对应的数据库不存在", redisMark));
        }
        StringRedisTemplate stringRedisTemplate = IOCContext.getBean(key, StringRedisTemplate.class);
        stringCache.put(key, stringRedisTemplate);
        return stringRedisTemplate;
    }

    /**
     * 获取Redis默认字符串模板
     *
     * @return
     */
    public static RedisTemplate getRedisTemplate() {
        return getRedisTemplate(null);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param redisMark 数据源标识
     * @return
     */
    public static RedisTemplate getRedisTemplate(String redisMark) {
        if (Objects.isNull(redisMark)) {
            redisMark = IOCContext.getBean(RedisDbProperties.class).getDefaultConfig();
        }
        /**
         * 获取缓存key
         */
        String key = getRedisTemplateBeanName(redisMark);
        if (restCache.containsKey(key)) {
            return restCache.get(key);
        }
        if (!IOCContext.containsBean(key)) {
            throw new RedisDbNotFoundException(MessageFormat.format("Redis数据库标识【{0}】对应的数据库不存在", redisMark));
        }
        RedisTemplate redisTemplate = IOCContext.getBean(key, RedisTemplate.class);
        restCache.put(key, redisTemplate);
        return redisTemplate;
    }

    /**
     * 获取StringRedisTemplate对象bean名称
     *
     * @param redisMark
     * @return
     */
    public static String getStringRedisTemplateBeanName(String redisMark) {

        Assert.notNull(redisMark, "Redis数据库标识不可为空");

        return MessageFormat.format("{0}_{1}", PREFIX_STRING, redisMark);
    }

    /**
     * 获取RedisTemplate对象bean名称
     *
     * @param redisMark
     * @return
     */
    public static String getRedisTemplateBeanName(String redisMark) {

        Assert.notNull(redisMark, "Redis数据库标识不可为空");

        return MessageFormat.format("{0}_{1}", PREFIX_REST, redisMark);
    }
}
