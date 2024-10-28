package com.emily.sample.request.entity;

/**
 * @author :  姚明洋
 * @since :  2024/10/25 下午2:31
 */
public class User {
    private Integer id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
