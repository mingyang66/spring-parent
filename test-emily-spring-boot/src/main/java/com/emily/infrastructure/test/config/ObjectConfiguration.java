package com.emily.infrastructure.test.config;

import com.emily.infrastructure.test.config.po.People;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @program: spring-parent
 * @description: 对象配置类
 * @author: Emily
 * @create: 2021/11/20
 */
@Configuration
public class ObjectConfiguration {

    @Bean
    @Order(value = 10)
    public People user() {
        People user = new People();
        user.setUsername("老师");
        // user.setOrder(10);
        return user;
    }

    @Bean("user1")
    @Order(value = 1)
    public People user1() {
        People user = new People();
        user.setUsername("学生");
        //user.setOrder(100);
        return user;
    }
 /*   @Bean("user2")
    @Order(value = 100)
    public OStudent user2() {
        OStudent user = new OStudent();
        user.setUsername("学生2");
        user.setDesc("学生2");
        return user;
    }*/
}
