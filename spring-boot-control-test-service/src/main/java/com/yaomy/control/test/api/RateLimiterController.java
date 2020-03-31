package com.yaomy.control.test.api;

import com.google.common.util.concurrent.RateLimiter;
import com.yaomy.control.test.po.User;
import com.yaomy.sgrain.ratelimit.annotation.RateLimit;
import com.yaomy.sgrain.submit.annotation.NoRepeatSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/rate/limiter")
    public String rateLimiter(){
        if(rateLimiter.tryAcquire(1, TimeUnit.SECONDS)){
            System.out.println("成功抢到小米10Pro，恭喜恭喜！");
        } else{
            System.out.println("sorry,抢光了，下次再来吧");
        }
        return "SUCCESS";
    }
    @GetMapping("/rate/limit")
    @RateLimit(permits = 2, name = {"name","age"}, time = 1, timeUnit = TimeUnit.SECONDS)
    @NoRepeatSubmit
    public String rateLimiter1(@Valid @RequestBody User user, String sgrain, HttpServletRequest request, HttpServletResponse response){
        System.out.println(user.getName()+"---"+user.getAge());
        //redisTemplate.opsForValue().set("test666", "888");
        return "SUCCESS";
    }
}
