package com.emily.infrastructure.sensitive.test;

import com.emily.infrastructure.sensitive.JsonSensitive;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description :  集合类型
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/28 2:45 PM
 */
@JsonSensitive
public class PeopleMap {
    private String username;
    private String password;
    private Map<String, SubMap> subMapMap = new HashMap<>();

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

    public Map<String, SubMap> getSubMapMap() {
        return subMapMap;
    }

    public void setSubMapMap(Map<String, SubMap> subMapMap) {
        this.subMapMap = subMapMap;
    }

    public static class SubMap{
        private String sub;

        public String getSub() {
            return sub;
        }

        public void setSub(String sub) {
            this.sub = sub;
        }
    }
}
