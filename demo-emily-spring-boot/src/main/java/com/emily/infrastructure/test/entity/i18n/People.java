package com.emily.infrastructure.test.entity.i18n;


import com.emily.infrastructure.language.convert.JsonI18nField;

/**
 * 人
 *
 * @author Emily
 * @since Created in 2023/4/19 3:43 PM
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
