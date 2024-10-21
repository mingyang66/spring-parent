package com.emily.simple.scheduled;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author :  Emily
 * @since :  2024/10/21 下午2:06
 */
@SpringBootApplication
@EnableScheduling
public class ScheduledBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ScheduledBootstrap.class, args);
    }
}
