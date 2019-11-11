package com.yaomy.control.redis;

import com.yaomy.control.redis.service.StringRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: Redis服务启动类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.redis.RedisBootStrap
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.yaomy.control.redis"})
public class RedisBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(RedisBootStrap.class, args);
    }
}
