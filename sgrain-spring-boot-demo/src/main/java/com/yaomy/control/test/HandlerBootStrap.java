package com.yaomy.control.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(scanBasePackages = {"com.sgrain.boot","com.yaomy.control.test"})
@ServletComponentScan(basePackages = {"com.sgrain.boot.actuator.filter"})
public class HandlerBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(HandlerBootStrap.class, args);
    }


}
