package com.yaomy.control.test.api;

import com.yaomy.sgrain.redis.utils.RedissonLockUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/redisson/test")
    public String testRedisson(){
        RLock lock = redissonClient.getLock("anylock123");
        Boolean falt = RedissonLockUtils.tryLock(lock, 10, TimeUnit.SECONDS);
        System.out.println("获取锁的结果是："+falt);
        RedissonLockUtils.unLock(lock);
        System.out.println("解锁成功");
        return "SUCCESS";
    }
}
