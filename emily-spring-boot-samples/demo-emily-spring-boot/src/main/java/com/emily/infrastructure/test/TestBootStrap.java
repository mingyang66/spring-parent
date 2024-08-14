package com.emily.infrastructure.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Emily
 */
@EnableFeignClients
@SpringBootApplication
public class TestBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(TestBootStrap.class, args);
    }

}
