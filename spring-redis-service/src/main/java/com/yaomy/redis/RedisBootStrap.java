package com.yaomy.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: Redis服务启动类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.redis.RedisBootStrap
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.yaomy.redis"})
public class RedisBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(RedisBootStrap.class, args);
    }
}
