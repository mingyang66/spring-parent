package com.emily.infrastructure.test.po.i18n;

import com.emily.infrastructure.common.i18n.ApiI18n;
import com.emily.infrastructure.common.i18n.ApiI18nProperty;

/**
 * @Description :  学生
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/17 3:34 PM
 */
@ApiI18n
public class Student {
    @ApiI18nProperty
    private String name;
    @ApiI18nProperty
    private int age;

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
