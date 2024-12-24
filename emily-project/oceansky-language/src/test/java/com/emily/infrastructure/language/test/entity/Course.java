package com.emily.infrastructure.language.test.entity;

import com.emily.infrastructure.language.annotation.I18nModel;
import com.emily.infrastructure.language.annotation.I18nProperty;

/**
 * @author :  Emily
 * @since :  2024/12/23 下午10:40
 */
@I18nModel
public class Course {
    @I18nProperty
    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
