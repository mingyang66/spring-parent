package com.emily.sample.rateLimter.controller;

import com.emily.infrastructure.rateLimiter.annotation.RateLimiterOperation;
import com.emily.sample.rateLimter.service.RateLimiterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author :  Emily
 * @since :  2024/8/30 上午10:36
 */
@RestController
public class RateLimiterController {
    private final RateLimiterService rateLimiterService;

    public RateLimiterController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("api/rate/limiter")
    public void rate(@RequestParam("key1") String key1, @RequestParam("key2") String key2) {
        rateLimiterService.rateLimiter(key1, key2);
    }

    @RateLimiterOperation(value = "SDK:limiter:api:limit", timeout = 5, timeunit = TimeUnit.MINUTES, threshold = 3, message = "您已触发访问限制，请等待几分钟后再试。")
    @GetMapping("api/rate/limiter1")
    public void rate1() {
        System.out.println("接口限流");
    }
}
