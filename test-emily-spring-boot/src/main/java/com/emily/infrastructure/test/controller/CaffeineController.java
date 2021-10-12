package com.emily.infrastructure.test.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 高速缓存控制器
 * @author: Emily
 * @create: 2021/08/28
 */
public class CaffeineController {
    public static <K, V> Cache<K, V> expiryCache() {
        Cache<K, V> caffeine = Caffeine.newBuilder()
                .initialCapacity(16)
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .maximumSize(1)
                .build();
        return caffeine;
    }
}
