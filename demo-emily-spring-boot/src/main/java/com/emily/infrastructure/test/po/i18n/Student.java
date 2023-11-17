package com.emily.infrastructure.test.po.i18n;

import com.emily.infrastructure.language.convert.JsonI18n;
import com.emily.infrastructure.language.convert.JsonI18nField;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * 学生
 *
 * @author Emily
 * @since Created in 2023/4/17 3:34 PM
 */
@JsonI18n
public class Student extends People {
    @JsonI18nField
    private String name;
    @JsonI18nField
    private int age;
    @JsonI18nField
    private List<String> like;
    @JsonI18nField
    private Map<String, String> data = Maps.newHashMap();
    @JsonI18nField
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
