package com.sgrain.boot.quartz;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sgrain.boot"})
public class QuartzBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(QuartzBootStrap.class, args);
    }


}
