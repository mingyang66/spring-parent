package com.emily.infrastructure.test.controller.redis;

import com.emily.infrastructure.redis.factory.BeanFactoryProvider;
import com.emily.infrastructure.redis.factory.RedisDbFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/7/5 下午22:14
 */
@RestController
@RequestMapping("api/redis")
public class RedisMessageController {
    @GetMapping("send")
    public void send() {
        RedisDbFactory.getStringRedisTemplate().convertAndSend("test", "测试消息");
        RedisDbFactory.getStringRedisTemplate("test1").convertAndSend("test1", "测试消息1");
    }

}
