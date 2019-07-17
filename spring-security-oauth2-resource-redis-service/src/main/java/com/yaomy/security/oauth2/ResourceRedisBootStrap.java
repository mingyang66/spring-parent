package com.yaomy.security.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: OAuth2 Redis模式下启动类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.ResourceRedisBootStrap
 * @Date: 2019/7/17 10:33
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.yaomy.security.oauth2"})
public class ResourceRedisBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(ResourceRedisBootStrap.class, args);
    }
}
