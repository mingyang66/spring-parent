package com.emily.infrastructure.redis.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.Arrays;

/**
 * 基于lua脚本的相关组件
 *
 * @author :  Emily
 * @since :  2023/11/9 11:07 PM
 */
public class LuaTools {
    /**
     * 基于lua脚本的限流工具
     *
     * @param redisTemplate redis模板工具类
     * @param key           限流键名
     * @param limit         限流阀值
     * @param expire        限流的时间窗口
     * @return true-访问有效，false-超过阀值
     */
    public static boolean limit(RedisTemplate redisTemplate, String key, int limit, int expire) {
        RedisScript<Long> script = RedisScript.of(new ClassPathResource("META-INF/scripts/limit.lua"), Long.class);
        // 0：超过阀值 1：访问有效
        Long count = (Long) redisTemplate.execute(script, Arrays.asList(key), limit, expire);
        return count == 1 ? true : false;
    }

    /**
     * 基于列表（List）的环, 列表永久有效
     *
     * @param redisTemplate redis 模板工具
     * @param key           环的键值
     * @param value         列表值
     * @param len           列表长度，即环上数据个数
     * @return 当前环（列表）长度
     */
    public static long circle(RedisTemplate redisTemplate, String key, Object value, long len) {
        return circle(redisTemplate, key, value, len, Duration.ZERO);
    }

    /**
     * 基于列表（List）的环
     * 1. 支持一直有效，interval 设置为<=0或null
     * 2. 支持设置有效时长，动态刷新，interval大于0
     *
     * @param redisTemplate redis 模板工具
     * @param key           环的键值
     * @param value         列表值
     * @param len           列表长度，即环上数据个数
     * @param expire        有效时长, 为null则永久有效
     * @return 当前环（列表）长度
     */
    public static long circle(RedisTemplate redisTemplate, String key, Object value, long len, Duration expire) {
        RedisScript<Long> script = RedisScript.of(new ClassPathResource("META-INF/scripts/list_circle.lua"), Long.class);
        if (expire == null) {
            expire = Duration.ZERO;
        }
        return (Long) redisTemplate.execute(script, Arrays.asList(key), value, len, expire.getSeconds());
    }
}
