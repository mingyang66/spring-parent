package com.emily.infrastructure.test.po;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/08/08
 */
public class User {
    private String username = "liming";
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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("User:"+username+ " finalize");
    }

    @Override
    public String toString() {
        return this.getUsername();
    }
}
