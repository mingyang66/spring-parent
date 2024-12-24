package com.emily.infrastructure.sensitize.test.entity;

import com.emily.infrastructure.sensitize.DesensitizeType;
import com.emily.infrastructure.sensitize.annotation.DesensitizeFlexibleProperty;
import com.emily.infrastructure.sensitize.annotation.DesensitizeNullProperty;
import com.emily.infrastructure.sensitize.annotation.DesensitizeModel;
import com.emily.infrastructure.sensitize.annotation.DesensitizeProperty;

/**
 * 人
 *
 * @author Emily
 * @since :  Created in 2023/5/14 4:50 PM
 */
@DesensitizeModel
public class People {
    @DesensitizeProperty
    private String username;
    private String password;
    @DesensitizeFlexibleProperty(value = {"email", "phone"}, target = "value", desensitizeType = {DesensitizeType.EMAIL, DesensitizeType.PHONE})
    private String key;
    private String value;
    @DesensitizeNullProperty
    private int age;
    @DesensitizeNullProperty
    private byte b;
    @DesensitizeNullProperty
    private short s;
    @DesensitizeNullProperty
    private long l;
    @DesensitizeNullProperty
    private double d;
    @DesensitizeNullProperty
    private float f;
    @DesensitizeNullProperty
    private char c;
    @DesensitizeNullProperty
    private String str;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public short getS() {
        return s;
    }

    public void setS(short s) {
        this.s = s;
    }

    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
