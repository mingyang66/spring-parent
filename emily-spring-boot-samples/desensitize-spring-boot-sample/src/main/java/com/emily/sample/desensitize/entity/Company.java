package com.emily.sample.desensitize.entity;

import com.emily.infrastructure.sensitive.DesensitizeType;
import com.emily.infrastructure.sensitive.annotation.DesensitizeModel;
import com.emily.infrastructure.sensitive.annotation.DesensitizeProperty;

/**
 * @author :  Emily
 * @since :  2024/12/7 下午4:10
 */
@DesensitizeModel
public class Company {
    private String companyName;
    @DesensitizeProperty(value = DesensitizeType.ADDRESS)
    private String address;
    @DesensitizeProperty(value = DesensitizeType.PHONE)
    private String phone;
    @DesensitizeProperty(value = DesensitizeType.EMAIL)
    private String email;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
