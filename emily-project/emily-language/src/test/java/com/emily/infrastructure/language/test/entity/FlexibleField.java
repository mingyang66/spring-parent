package com.emily.infrastructure.language.test.entity;

import com.emily.infrastructure.language.i18n.annotation.I18nFlexibleProperty;
import com.emily.infrastructure.language.i18n.annotation.I18nModel;

/**
 * @author :  Emily
 * @since :  2024/12/24 下午1:36
 */
@I18nModel
public class FlexibleField {
    @I18nFlexibleProperty(value = {"username", "email"}, target = "value7")
    private String key7;
    private String value7;
    /**
     * 目标字段不存在
     */
    @I18nFlexibleProperty(value = {"username", "email"}, target = "value0")
    private String key0;
    /**
     * 目标字段存在
     */
    @I18nFlexibleProperty(value = {"username", "email"}, target = "value1")
    private String key1;
    private String value1;
    /**
     * 目标字段未指定
     */
    @I18nFlexibleProperty(value = {"username", "email"}, target = "")
    private String key2;
    private String value2;
    /**
     * 目标字段存在，没有传值
     */
    @I18nFlexibleProperty(value = {"username", "email"}, target = "value3")
    private String key3;
    private String value3;
    /**
     * 原值未指定字段，目标字段存在
     */
    @I18nFlexibleProperty(target = "value4")
    private String key4;
    private String value4;
    /**
     * 目标字段不存在
     */
    @I18nFlexibleProperty(value = {"username", "email"}, target = "value5")
    private String key5;
    /**
     * 目标字段存在，没有传值
     */
    @I18nFlexibleProperty(value = {"username", "email"}, target = "value6")
    private String key6;
    private String value6;

    public String getKey7() {
        return key7;
    }

    public void setKey7(String key7) {
        this.key7 = key7;
    }

    public String getValue7() {
        return value7;
    }

    public void setValue7(String value7) {
        this.value7 = value7;
    }

    public String getKey6() {
        return key6;
    }

    public void setKey6(String key6) {
        this.key6 = key6;
    }

    public String getValue6() {
        return value6;
    }

    public void setValue6(String value6) {
        this.value6 = value6;
    }

    public String getKey0() {
        return key0;
    }

    public void setKey0(String key0) {
        this.key0 = key0;
    }

    public String getKey5() {
        return key5;
    }

    public void setKey5(String key5) {
        this.key5 = key5;
    }

    public String getKey4() {
        return key4;
    }

    public void setKey4(String key4) {
        this.key4 = key4;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getKey3() {
        return key3;
    }

    public void setKey3(String key3) {
        this.key3 = key3;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }
}

