package com.emily.infrastructure.test.controller.rateLimiter;

import com.emily.infrastructure.rateLimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

/**
 * @author :  姚明洋
 * @since :  2024/8/30 上午11:12
 */
@Service
public class RateLimiterServiceImpl implements RateLimiterService {
    @Override
    @RateLimiter(prefix="SDK:limiter", expire = 1000)
    public void rateLimiter() {
        System.out.println("---------------");
    }
}
