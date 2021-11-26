package com.emily.infrastructure.test.config.po;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @program: spring-parent
 * @description: 老师
 * @author: Emily
 * @create: 2021/11/20
 */
//@Component
//@Priority(100)
@Order(value = 1000)
public class OTeacher extends People implements Ordered{
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}
