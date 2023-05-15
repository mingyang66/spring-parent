package com.emily.infrastructure.sensitive.test;

import com.emily.infrastructure.sensitive.JsonFlexField;
import com.emily.infrastructure.sensitive.JsonSensitive;
import com.emily.infrastructure.sensitive.JsonSimField;
import com.emily.infrastructure.sensitive.SensitiveType;

/**
 * @Description : 人
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 4:50 PM
 */
@JsonSensitive
public class People {
    @JsonSimField
    private String username;
    private String password;
    @JsonFlexField(fieldKeys = {"email", "phone"}, fieldValue = "value", types = {SensitiveType.EMAIL, SensitiveType.PHONE})
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
