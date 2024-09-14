package com.emily.infrastructure.test.controller.rateLimiter.config;

import com.emily.infrastructure.rateLimiter.annotation.RateLimiter;
import com.emily.infrastructure.rateLimiter.interceptor.DefaultRateLimiterMethodInterceptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author :  Emily
 * @since :  2024/9/14 上午11:09
 */
@Component
public class CustomRateLimiterInterceptor extends DefaultRateLimiterMethodInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    public CustomRateLimiterInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public int getVisitedTimes(String key) {
        String countStr = stringRedisTemplate.opsForValue().get(key);
        return countStr == null ? 0 : Integer.parseInt(countStr);
    }

    @Override
    public void addVisitedTimes(String key, RateLimiter rateLimiter) {
        stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, rateLimiter.timeout(), rateLimiter.timeunit());
    }
}
