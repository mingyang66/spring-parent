package com.yaomy.control.redis.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 单元测试类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.redis.service.StringRedisServiceTest
 * @Date: 2019/7/16 14:55
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StringRedisServiceTest {
    @Autowired
    private StringRedisService redisService;

    @Test
    public void setKey(){
        Boolean flag = redisService.set("test1", "一直有效");
        System.out.println(flag);
        String value = redisService.get("test1");
        System.out.println(value);
        Long expireTime = redisService.getExpire("test1");
        System.out.println(expireTime);

        Boolean flag2 = redisService.set("test2", "30秒过期", 30l, TimeUnit.SECONDS);
        System.out.println(flag2);
        String value2 = redisService.get("test2");
        System.out.println(value2);
        Long expireTime2 = redisService.getExpire("test2", TimeUnit.SECONDS);
        System.out.println(expireTime2);
    }
}
