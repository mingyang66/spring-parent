package com.emily.infrastructure.test.service.impl;

import com.emily.infrastructure.test.entity.cache.CacheUser;
import com.emily.infrastructure.test.service.CacheService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author :  Emily
 * @since :  2024/7/18 上午11:14
 */
@Service
public class CacheServiceImpl implements CacheService {
    @Cacheable(cacheNames = "user:test", key = "#user.id")
    @Override
    public String cache(CacheUser user) {
        return "testfddsf";
    }
}
