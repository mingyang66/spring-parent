package com.emily.infrastructure.test.po;

import com.emily.infrastructure.sensitive.JsonSensitive;
import com.emily.infrastructure.sensitive.JsonSimField;
import com.emily.infrastructure.sensitive.SensitiveType;
import org.springframework.core.Ordered;

/**
 * @program: spring-parent
 * 
 * @author Emily
 * @since 2021/08/08
 */
@JsonSensitive
public class User implements Ordered {
    private String username;
    @JsonSimField(SensitiveType.DEFAULT)
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
