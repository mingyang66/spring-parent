package com.emily.infrastructure.test.po.i18n;

import com.emily.infrastructure.common.i18n.ApiI18n;
import com.emily.infrastructure.common.i18n.ApiI18nProperty;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @Description :  学生
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/17 3:34 PM
 */
@ApiI18n
public class Student extends People {
    @ApiI18nProperty
    private String name;
    @ApiI18nProperty
    private int age;
    @ApiI18nProperty
    private List<String> like;
    @ApiI18nProperty
    private Map<String, String> data = Maps.newHashMap();
    @ApiI18nProperty
    private String[] s = {"红薯", "电影"};

    public String[] getS() {
        return s;
    }

    public void setS(String[] s) {
        this.s = s;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    private int[] array = new int[]{1, 2, 3};

    public List<String> getLike() {
        return like;
    }

    public void setLike(List<String> like) {
        this.like = like;
    }

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
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
