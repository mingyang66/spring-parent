package com.emily.infrastructure.sample.web.entity.json;


import com.emily.infrastructure.sensitize.annotation.DesensitizeProperty;

/**
 * 动物
 *
 * @author Emily
 * @since Created in 2023/4/19 4:06 PM
 */
public class Animal {
    @DesensitizeProperty
    public String animalType;
}
