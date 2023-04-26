package com.emily.infrastructure.test.po.i18n;

import com.emily.infrastructure.common.i18n.ApiI18nProperty;

/**
 * @Description :  人
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/19 3:43 PM
 */
public class People {
    @ApiI18nProperty
    private String food;

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }
}
