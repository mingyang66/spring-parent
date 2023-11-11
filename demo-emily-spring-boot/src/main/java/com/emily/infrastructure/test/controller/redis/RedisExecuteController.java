package com.emily.infrastructure.test.controller.redis;

import com.emily.infrastructure.redis.factory.RedisDbFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author :  Emily
 * @since :  2023/11/4 2:45 PM
 */
@RestController
@RequestMapping("api/redis")
public class RedisExecuteController {
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("ex")
    public void ex() {
        String s = RedisDbFactory.getStringRedisTemplate().execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                for (int i = 0; i < 10; i++) {
                    stringRedisConnection.setEx("test", 10, "1");
                }
                return null;
            }
        });
    }
    @GetMapping("trans")
    public void trans(){
        List<Object> list1 = (List<Object>) redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
               // operations.multi();
                operations.opsForValue().set("test","12",10, TimeUnit.SECONDS);
                operations.opsForValue().set("test1","12",10, TimeUnit.SECONDS);
                operations.opsForValue().set("test2","12",10, TimeUnit.SECONDS);
               // return operations.exec();
                return null;
            }
        });
        System.out.println("Number of items added to set: " + list1.get(0));
    }
}
