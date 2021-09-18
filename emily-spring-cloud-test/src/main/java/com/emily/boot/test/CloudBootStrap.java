package com.emily.boot.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Emily
 */
@SpringBootApplication
@EnableFeignClients
public class CloudBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(CloudBootStrap.class, args);
    }

}
