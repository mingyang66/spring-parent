package com.emily.infrastructure.test.controller.rateLimiter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  姚明洋
 * @since :  2024/8/30 上午10:36
 */
@RestController
public class RateLimiterController {
    private final RateLimiterService rateLimiterService;

    public RateLimiterController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("api/rate/limiter")
    public void rate() {
        rateLimiterService.rateLimiter();
    }
}
