package com.emily.sample.validation.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author :  Emily
 * @since :  2024/10/28 下午6:00
 */
public class User {
    @NotEmpty(message = "id不可为空")
    private String id;
    @NotEmpty(message = "name不可以为空")
    private String name;
    @Valid
    @NotNull(message = "school不可为空")
    private School school;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public static class School {
        @NotEmpty(message = "地址不可为空")
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
