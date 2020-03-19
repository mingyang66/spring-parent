package com.yaomy.sgrain.redis.utils;

import org.redisson.api.RFuture;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: Redisson分布式锁工具类
 * @author: 姚明洋
 * @create: 2020/03/18
 */
public class RedissonLockUtils {
    /**
     * @Description 同步等待锁，默认开启看门狗定时任务，过期时间是30s,过期时间小于等于20s时执行看门狗任务
     * @param lock
     */
    public static void lock(RLock lock){
        lock.lock();
    }

    /**
     * @Description 同步等待获取锁，过期时间是leaseTime
     * @param lock 锁对象
     * @param leaseTime 过期时间
     * @param unit 单位
     */
    public static void lock(RLock lock, long leaseTime, TimeUnit unit){
        lock.lock(leaseTime, unit);
    }
    /**
     * @Description 同步等待获取锁，等待时间是time,获取成功返回true,否则返回false;
     * 默认锁过期时间是30s,如果任务未完成，后端会有一个看门狗的定时任务去给锁续期，当锁的有效期小于等于20s时，看门狗会将过期时间设置为30s,直到任务结束
     * @param lock 锁对象
     * @return TRUE|FALSE
     */
    public static Boolean tryLock(RLock lock) {
        return lock.tryLock();
    }
    /**
     * @Description 同步等待获取锁，等待时间是time,获取成功返回true,否则返回false;
     * 默认锁过期时间是30s,如果任务未完成，后端会有一个看门狗的定时任务去给锁续期，当锁的有效期小于等于20s时，看门狗会将过期时间设置为30s,直到任务结束
     * @param lock 锁对象
     * @param waitTime 等待锁的最长时间
     * @param unit 时间单位
     * @return TRUE|FALSE
     */
    public static Boolean tryLock(RLock lock, long waitTime, TimeUnit unit) {
        try {
            return lock.tryLock(waitTime, unit);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * @Description 根据指定的锁lockKey，最长等待waitTime设置过期时间为leaseTime的锁
     * @param lock 锁对象
     * @param waitTime 等待锁的最长时间
     * @param leaseTime 锁过期时间
     * @param unit 时间单位
     * @return TRUE|FALSE
     */
    public static Boolean tryLock(RLock lock, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }
    /**
     * @Description 释放锁，会考虑锁重入问题
     * @param lock 锁对象
     */
    public static void unLock(RLock lock){
        lock.unlock();
    }
    /**
     * @Description 独立于锁状态的释放锁,不会考虑当前线程被加锁的次数，只是简单粗暴的删除
     * @param lock 锁对象
     * @return TRUE  如果锁存在， FALSE 锁不存在
     */
    public static Boolean forceUnlock(RLock lock){
        return lock.forceUnlock();
    }

    /**
     * @Description 异步获取锁，等待锁指定的时间，后端起一个看门狗定时任务
     * @param lock 锁对象
     * @param waitTime 等待锁时间
     * @param unit 单位
     * @return RFuture对象
     */
    public static RFuture<Boolean> tryLockAsync(RLock lock, long waitTime, TimeUnit unit){
        return lock.tryLockAsync(waitTime, unit);
    }
    /**
     * @Description 异步获取锁，等待锁指定的时间
     * @param lock 锁对象
     * @param waitTime 等待锁时间
     * @param leaseTime 锁过期时间
     * @param unit 单位
     * @return RFuture对象
     */
    public static RFuture<Boolean> tryLockAsync(RLock lock, long waitTime, long leaseTime, TimeUnit unit){
        return lock.tryLockAsync(waitTime, leaseTime, unit);
    }

    /**
     * @Description 异步获取锁
     * @param lock 锁对象
     * @return RFuture对象
     */
    public static RFuture<Boolean> tryLockAsync(RLock lock){
        return lock.tryLockAsync();
    }
}
