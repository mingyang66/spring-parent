package com.yaomy.control.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoBootStrap {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DemoBootStrap.class);
        application.run(args);
    }


}
