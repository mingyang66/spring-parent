package com.emily.infrastructure.rpc.client.example;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description: 返回结果
 * @author: Emily
 * @create: 2021/09/17
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 7001418419812113267L;
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
