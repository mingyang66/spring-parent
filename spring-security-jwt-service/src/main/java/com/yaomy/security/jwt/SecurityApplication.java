package com.yaomy.security.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.SecurityApplication
 * @Author: 姚明洋
 * @Date: 2019/6/28 13:21
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.yaomy.security"})
public class SecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }
}
