package com.yaomy.control.test.po;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.Map;

public class People {

    private String username;
    private Map<String, String> properties;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }
    @JsonAnySetter
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
