package com.emily.infrastructure.language.test.entity;

import com.emily.infrastructure.language.i18n.annotation.I18nModel;
import com.emily.infrastructure.language.i18n.annotation.I18nProperty;

/**
 * @author :  Emily
 * @since :  2024/12/23 下午10:35
 */
@I18nModel
public class User {
    @I18nProperty
    private String username;
    private String password;

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
