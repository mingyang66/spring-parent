package com.emily.infrastructure.test.po;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/08/08
 */
public class User {
    private String username;
    private String password;
    private Job job;

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
}
