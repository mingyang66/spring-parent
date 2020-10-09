package com.sgrain.boot.quartz.config;

import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/09/30
 */
@Configuration
public class BeanA {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
