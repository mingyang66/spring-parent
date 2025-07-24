package com.emily.sample.validation.entity;

import com.emily.infrastructure.validation.annotation.IntRange;
import com.emily.infrastructure.validation.annotation.NumericRange;
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
    @IntRange(min = 4, max = 8, message = "年龄不符合规定")
    private int age;
    @IntRange(min = 1, max = 4, message = "宽度不符合规定")
    private Integer width;
    @NumericRange(min = 2, max = 100, message = "身高不符合规定")
    private String height;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

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
