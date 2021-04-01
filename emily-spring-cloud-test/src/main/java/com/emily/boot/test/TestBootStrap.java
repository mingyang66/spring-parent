package com.emily.boot.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TestBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(TestBootStrap.class, args);
    }

}
