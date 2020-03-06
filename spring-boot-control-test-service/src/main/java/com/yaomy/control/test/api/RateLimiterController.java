package com.yaomy.control.test.api;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 限流控制器
 * @author: 姚明洋
 * @create: 2020/03/06
 */
@RestController
public class RateLimiterController {
    private RateLimiter rateLimiter = RateLimiter.create(2);

    @GetMapping("/rate/limiter")
    public String rateLimiter(){
        if(rateLimiter.tryAcquire(1, TimeUnit.SECONDS)){
            System.out.println("成功抢到小米10Pro，恭喜恭喜！");
        } else{
            System.out.println("sorry,抢光了，下次再来吧");
        }
        return "SUCCESS";
    }
}
