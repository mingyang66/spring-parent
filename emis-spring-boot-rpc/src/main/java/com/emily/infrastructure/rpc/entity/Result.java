package com.emily.infrastructure.rpc.entity;

/**
 * @program: spring-parent
 * @description: 返回结果
 * @author: Emily
 * @create: 2021/09/17
 */
public class Result {
    private int id;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
