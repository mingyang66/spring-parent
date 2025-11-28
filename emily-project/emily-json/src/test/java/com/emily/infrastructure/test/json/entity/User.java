package com.emily.infrastructure.test.json.entity;

import java.util.List;
import java.util.Map;

/**
 * @author :  Emily
 * @since :  2024/6/21 下午1:17
 */
public class User {
    private String username;
    private String password;

    private Like like;
    private List<Like> list;
    private Map<String, Like> dataMap;

    public Like getLike() {
        return like;
    }

    public void setLike(Like like) {
        this.like = like;
    }

    public List<Like> getList() {
        return list;
    }

    public void setList(List<Like> list) {
        this.list = list;
    }

    public Map<String, Like> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Like> dataMap) {
        this.dataMap = dataMap;
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

    public static class Like {
        private String name;
        private int height;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
