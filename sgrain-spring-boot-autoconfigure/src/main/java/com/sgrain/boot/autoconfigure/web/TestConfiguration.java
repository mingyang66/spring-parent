package com.sgrain.boot.autoconfigure.web;

import org.apache.catalina.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @program: spring-parent
 * @description: s
 * @author: 姚明洋
 * @create: 2020/06/05
 */
@Configuration
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
public class TestConfiguration {

    @Bean
    public User userBean(){
        System.out.println("------sdf-----------");
        return null;
    }
}
