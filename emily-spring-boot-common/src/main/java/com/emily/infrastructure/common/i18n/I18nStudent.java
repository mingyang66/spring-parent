package com.emily.infrastructure.common.i18n;

/**
 * @Description :  多语言
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/17 10:17 AM
 */
@ApiI18n
public class I18nStudent {
    @ApiI18nProperty
    private String name;
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
