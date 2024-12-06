package com.emily.infrastructure.sample.web.entity;

import com.emily.infrastructure.sensitive.DesensitizeType;
import com.emily.infrastructure.sensitive.annotation.DesensitizeModel;
import com.emily.infrastructure.sensitive.annotation.DesensitizeProperty;
import org.springframework.core.Ordered;

/**
 * @author Emily
 * @program: spring-parent
 * @since 2021/08/08
 */
@DesensitizeModel
public class User implements Ordered {
    private String username;
    @DesensitizeProperty(DesensitizeType.DEFAULT)
    private String password;
    private Job job;
    private int order;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return this.getUsername();
    }

    @Override
    public int getOrder() {
        return order;
    }
}
