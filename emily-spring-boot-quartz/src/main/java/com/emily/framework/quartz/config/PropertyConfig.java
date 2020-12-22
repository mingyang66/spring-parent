package com.emily.framework.quartz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/10/13
 */
@PropertySource(value = {"classpath:test.properties"})
@Configuration(proxyBeanMethods = false)
public class PropertyConfig implements ApplicationRunner {
    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(environment.getProperty("test.a"));
        System.out.println(environment.getProperty("test.b"));
    }
}
