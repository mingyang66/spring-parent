package com.emily.sample.request;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author :  Emily
 * @since :  2024/10/25 上午10:00
 */
@EnableScheduling
@SpringBootApplication
public class RequestBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(RequestBootstrap.class, args);
    }
}
