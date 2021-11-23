package com.emily.infrastructure.test.config.po;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Priority;

/**
 * @program: spring-parent
 * @description: 学生
 * @author: Emily
 * @create: 2021/11/20
 */
//@Component
//@Priority(1)
@Order(value = 100)
public class OStudent extends People implements Ordered{
    private String desc;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
