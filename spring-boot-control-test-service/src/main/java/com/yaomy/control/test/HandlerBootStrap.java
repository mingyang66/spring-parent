package com.yaomy.control.test;


import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.yaomy.control","com.yaomy.sgrain"}, exclude = {RedissonAutoConfiguration.class})
public class HandlerBootStrap {

    public static void main(String[] args) {
        SpringApplication.run(HandlerBootStrap.class, args);
    }

}
