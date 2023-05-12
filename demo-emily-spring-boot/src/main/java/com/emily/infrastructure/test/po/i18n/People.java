package com.emily.infrastructure.test.po.i18n;


import com.emily.infrastructure.language.convert.JsonI18nField;

/**
 * @Description :  人
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/19 3:43 PM
 */
public class People {
    @JsonI18nField
    private String food;

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }
}
