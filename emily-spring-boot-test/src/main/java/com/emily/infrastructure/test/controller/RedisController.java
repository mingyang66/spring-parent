package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.datasource.redis.factory.RedisDbFactory;
import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 缓存测试
 * @author: Emily
 * @create: 2021/07/14
 */
@RestController
@RequestMapping("redis")
public class RedisController {

    @GetMapping("get1")
    public String get1(){
        RedisDbFactory.getStringRedisTemplate().opsForValue().set("test", "123", 12, TimeUnit.MINUTES);
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("te", 12);
        dataMap.put("te2", 12);
        dataMap.put("te3", "哈哈");
        RedisDbFactory.getRedisTemplate().opsForValue().set("test1", dataMap, 1, TimeUnit.MINUTES);
        RedisDbFactory.getRedisTemplate("one").opsForValue().set("one", "adf", 1, TimeUnit.MINUTES);
        return RedisDbFactory.getStringRedisTemplate("default").opsForValue().get("test");
    }
    @GetMapping("get2")
    public Object get2(){
        RedisDbFactory.getRedisTemplate("test").opsForValue().set("test", "123", 12, TimeUnit.MINUTES);
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("te", 12);
        dataMap.put("te2", 12);
        dataMap.put("te3", "年好吗");
        RedisDbFactory.getRedisTemplate("test").opsForValue().set("test1", dataMap, 1, TimeUnit.MINUTES);
        RedisDbFactory.getRedisTemplate("one").opsForValue().set("one", dataMap, 1, TimeUnit.MINUTES);
        return RedisDbFactory.getRedisTemplate("test").opsForValue().get("test");
    }
}
