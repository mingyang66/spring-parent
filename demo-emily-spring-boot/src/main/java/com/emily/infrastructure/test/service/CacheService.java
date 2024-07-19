package com.emily.infrastructure.test.service;

import com.emily.infrastructure.test.entity.cache.CacheUser;

/**
 * @author :  Emily
 * @since :  2024/7/18 上午11:13
 */
public interface CacheService {
    String cache(CacheUser user);
}
