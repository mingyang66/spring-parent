package com.yaomy.sgrain.redis.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: Redis分布式锁
 * @author: 姚明洋
 * @create: 2020/03/17
 */
public class RedisLockUtils {
    /**
     * 释放锁lua脚本
     */
    private static final String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
    /**
     * 释放锁成功返回值
     */
    private static final Long RELEASE_LOCK_SUCCESS = 1L;
    /**
     * 自动过期释放锁成功返回值
     */
    private static final Long RELEASE_LOCK_AUTO_SUCCESS = 0L;
    /**
     * @Description 尝试获取分布式锁, 过期时间单位默认：毫秒
     * @param redisTemplate Redis客户端对象
     * @param lockKey 锁
     * @param value 唯一标识
     * @param expireTime 过期时间
     * @return 是否获取成功
     */
    public static Boolean tryLock(RedisTemplate redisTemplate, String lockKey, String value, long expireTime){
       return tryLock(redisTemplate, lockKey, value, expireTime, TimeUnit.MILLISECONDS);
    }
    /**
     * @Description 尝试获取分布式锁
     * @param redisTemplate Redis客户端对象
     * @param lockKey 锁
     * @param value 唯一标识
     * @param expireTime 过期时间
     * @param util 单位
     * @return 是否获取成功
     */
    public static Boolean tryLock(RedisTemplate redisTemplate, String lockKey, String value, long expireTime, TimeUnit util){
        long currentTime = System.currentTimeMillis();
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, value, expireTime, util);
        if(System.currentTimeMillis() - currentTime >= expireTime){
            return Boolean.FALSE;
        }
        if(Boolean.TRUE.equals(result)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * @Description 释放锁
     * @param redisTemplate Redis客户端对象
     * @param lockKey 锁
     * @param value 唯一标识
     * @return
     */
    public static Boolean releaseLock(RedisTemplate redisTemplate, String lockKey, String value){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
        //释放锁成功，或锁自动过期
        if(RELEASE_LOCK_SUCCESS.equals(result) || RELEASE_LOCK_AUTO_SUCCESS.equals(result)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     *
     * @param redisTemplate  Redis客户端对象
     * @param lockKey 锁
     * @return 锁过期时间，单位：毫秒
     */
    public static long getLockExpire(RedisTemplate redisTemplate, String lockKey){
        return getLockExpire(redisTemplate, lockKey, TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @param redisTemplate  Redis客户端对象
     * @param lockKey 锁
     * @return 锁过期时间
     */
    public static long getLockExpire(RedisTemplate redisTemplate, String lockKey, TimeUnit unit){
        return redisTemplate.opsForValue().getOperations().getExpire(lockKey, unit);
    }

    /**
     * @Description 设置过期时间
     * @param redisTemplate Redis客户端
     * @param lockKey 锁
     * @param expire 过期时间
     * @return true|false
     */
    public static Boolean expireLock(RedisTemplate redisTemplate, String lockKey, long expire){
        return expireLock(redisTemplate, lockKey, expire, TimeUnit.MILLISECONDS);
    }
    /**
     * @Description 设置过期时间
     * @param redisTemplate Redis客户端
     * @param lockKey 锁
     * @param expire 过期时间
     * @param unit 单位
     * @return true|false
     */
    public static Boolean expireLock(RedisTemplate redisTemplate, String lockKey, long expire, TimeUnit unit){
        return redisTemplate.opsForValue().getOperations().expire(lockKey, expire, unit);
    }
}
