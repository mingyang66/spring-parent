package com.emily.infrastructure.test.config.po;

import org.springframework.core.Ordered;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/11/20
 */
public class People implements Ordered{
    private int order;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
