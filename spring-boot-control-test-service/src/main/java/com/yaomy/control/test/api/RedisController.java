package com.yaomy.control.test.api;

import com.yaomy.sgrain.ratelimit.annotation.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: Redis控制器
 * @author: 姚明洋
 * @create: 2020/03/19
 */
@RestController
public class RedisController {
    //@Autowired
    //private RedisTemplate redisTemplate;

    @GetMapping("/redis/test")
    @RateLimit(name = {"username","password"}, permits = 10, timeUnit = TimeUnit.SECONDS)
    public String testRedisson(){
       // redisTemplate.opsForValue().set("test", "测试数据abc123");

        return "SUCCESS";
    }
    @GetMapping("/redis/test1")
    public String testRedisson1(){
        //redisTemplate.opsForValue().set("test", "测试数据abc123");

        return "SUCCESS";
    }
}
