package com.emily.infrastructure.language.test.entity;

import com.emily.infrastructure.language.annotation.I18nModel;
import com.emily.infrastructure.language.annotation.I18nProperty;

/**
 * @author :  Emily
 * @since :  2024/12/23 下午10:36
 */
@I18nModel
public class Student {
    @I18nProperty
    private String name;
    private int age;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
