package com.yaomy.control.test;


import com.yaomy.sgrain.ratelimit.config.RateLimitAutoConfiguration;
import com.yaomy.sgrain.redis.config.RedisSgrainAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.yaomy.control","com.yaomy.sgrain"})
public class HandlerBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(HandlerBootStrap.class, args);
    }

}
