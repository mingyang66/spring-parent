package com.emily.infrastructure.sample.web.controller.rateLimiter;

import com.emily.infrastructure.rateLimiter.annotation.RateLimiterOperation;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author :  Emily
 * @since :  2024/8/30 上午11:12
 */
@Service
public class RateLimiterServiceImpl implements RateLimiterService {
    @Override
    @RateLimiterOperation(value = "SDK:limiter:%s:%s", timeout = 5, timeunit = TimeUnit.MINUTES, threshold = 3, message = "您已触发访问限制，请等待几分钟后再试。")
    public void rateLimiter(String key1, String key2) {
        System.out.println("---------------");
    }
}
