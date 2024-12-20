package com.emily.infrastructure.sample.web.controller.rateLimiter.config;

import com.emily.infrastructure.rateLimiter.interceptor.DefaultRateLimiterMethodInterceptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
    public int before(String key) {
        String countStr = stringRedisTemplate.opsForValue().get(key);
        return countStr == null ? 0 : Integer.parseInt(countStr);
    }

    @Override
    public void after(String key, long timeout, TimeUnit timeunit) {
        stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, timeout, timeunit);
    }
}
