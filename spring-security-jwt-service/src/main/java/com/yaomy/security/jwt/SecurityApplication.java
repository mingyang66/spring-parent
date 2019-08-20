package com.yaomy.security.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.yaomy.security.jwt"})
public class SecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }
}
