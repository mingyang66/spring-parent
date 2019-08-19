package com.yaomy.handler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.yaomy.control.exception","com.yaomy.handler"})
public class HandlerBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(HandlerBootStrap.class, args);
    }
}
