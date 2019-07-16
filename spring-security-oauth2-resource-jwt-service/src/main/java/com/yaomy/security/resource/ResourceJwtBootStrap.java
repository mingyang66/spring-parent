package com.yaomy.security.resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: 资源服务器启动类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.resource.ResourceBootStrap
 * @Date: 2019/7/12 14:43
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.yaomy.security.resource"})
public class ResourceJwtBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(ResourceJwtBootStrap.class, args);
    }
}
