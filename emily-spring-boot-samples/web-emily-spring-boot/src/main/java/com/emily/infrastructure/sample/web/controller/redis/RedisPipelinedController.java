package com.emily.infrastructure.sample.web.controller.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 管道
 *
 * @author :  Emily
 * @since :  2023/11/1 9:40 PM
 */
@RestController
@RequestMapping("api/redis/pipelined")
public class RedisPipelinedController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("get")
    public void get() {
        stringRedisTemplate.opsForValue().set("test1", "2", 10, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set("test2", "2", 10, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set("test3", "2", 10, TimeUnit.SECONDS);
    }

    @GetMapping("pipe")
    public void pipelined() {
        List<Object> results = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                for (int i = 0; i < 10; i++) {
                    stringRedisConn.setEx("test" + i, 100, i + 100 + "");
                }
                return null;
            }
        });
        System.out.println(results);
    }

    @GetMapping("ser")
    public void ser() {
        stringRedisTemplate.executePipelined(new SessionCallback<String>() {
            @Override
            public String execute(RedisOperations operations) throws DataAccessException {
                operations.opsForValue().set("test", "t", 10, TimeUnit.SECONDS);
                operations.opsForValue().set("test", "t", 10, TimeUnit.SECONDS);
                operations.opsForValue().set("test", "t", 10, TimeUnit.SECONDS);
                operations.opsForValue().set("test", "t", 10, TimeUnit.SECONDS);
                return null;
            }
        });
    }

    @GetMapping("exe")
    public void exe() {
        stringRedisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                stringRedisConn.set("test", "1");
                stringRedisConn.setEx("test1", 100, "2");
                String s = stringRedisConn.get("test");
                return null;
            }
        });
    }
}
