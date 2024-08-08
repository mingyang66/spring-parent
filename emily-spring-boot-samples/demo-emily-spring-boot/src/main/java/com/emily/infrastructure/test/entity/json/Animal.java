package com.emily.infrastructure.test.entity.json;


import com.emily.infrastructure.sensitive.annotation.JsonSimField;

/**
 * 动物
 *
 * @author Emily
 * @since Created in 2023/4/19 4:06 PM
 */
public class Animal {
    @JsonSimField
    public String animalType;
}
