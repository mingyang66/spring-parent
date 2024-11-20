package com.emily.infrastructure.sample.web.test;

/**
 * @author :  Emily
 * @since :  2024/6/8 下午5:17
 */
public class People {
    private String name;
    private int age;
    private int height;

    public People() {
    }

    public People(String name, int age, int height) {
        this.name = name;
        this.age = age;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

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
}
