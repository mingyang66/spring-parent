package com.emily.infrastructure.language.test.entity;

import com.emily.infrastructure.language.annotation.I18nFlexibleProperty;
import com.emily.infrastructure.language.annotation.I18nModel;

/**
 * @author :  姚明洋
 * @since :  2024/12/24 下午1:36
 */
@I18nModel
public class FlexibleField {
    @I18nFlexibleProperty(value = {"username", "email"}, target = "key2")
    private String key1;
    private String key2;

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }
}

