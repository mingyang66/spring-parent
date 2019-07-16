package com.yaomy.security.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: Spring Security oauth2启动类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.OAuth2BootStrap
 * @Date: 2019/7/5 16:10
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.yaomy.security.oauth2"})
public class OAuth2RedisBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(OAuth2RedisBootStrap.class, args);
    }
}
