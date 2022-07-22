package com.emily.infrastructure.test.po.sensitive;

import com.emily.infrastructure.common.sensitive.annotation.Sensitive;
import com.emily.infrastructure.common.sensitive.enumeration.Logic;
import com.emily.infrastructure.common.sensitive.enumeration.Strategy;

/**
 * @Description :  脱敏测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 5:34 下午
 */
public class Person {
    @Sensitive(strategy = Strategy.ENTITY)
    private String username;
    /**
     * 真实姓名
     */
    @Sensitive
    private String realName;
    /**
     * 地址
     */
    private String address;
    /**
     * 电话号码
     */
    @Sensitive(strategy = Strategy.ENTITY,logic = Logic.PHONE)
    private String phoneNumber;
    /**
     * 身份证号码
     */
    @Sensitive
    private String idCard;
    private int age;
    private byte b;
    private short s;
    private long l;
    private char c;
    private double dd;
    private float fl;

    public double getDd() {
        return dd;
    }

    public void setDd(double dd) {
        this.dd = dd;
    }

    public float getFl() {
        return fl;
    }

    public void setFl(float fl) {
        this.fl = fl;
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

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}
