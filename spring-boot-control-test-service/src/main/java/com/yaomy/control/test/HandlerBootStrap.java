package com.yaomy.control.test;


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
