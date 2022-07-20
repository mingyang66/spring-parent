package com.emily.infrastructure.test.po.sensitive;

import com.emily.infrastructure.common.sensitive.annotation.Sensitive;
import com.emily.infrastructure.common.sensitive.strategy.SensitiveStrategy;

/**
 * @Description :  脱敏测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 5:34 下午
 */
public class Person {
    @Sensitive
    private String username;
    /**
     * 真实姓名
     */
    @Sensitive(strategy = SensitiveStrategy.USERNAME)
    private String realName;
    /**
     * 地址
     */
    private String address;
    /**
     * 电话号码
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phoneNumber;
    /**
     * 身份证号码
     */
    @Sensitive(strategy = SensitiveStrategy.ID_CARD)
    private String idCard;

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
