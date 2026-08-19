package com.emily.infrastructure.test.security.entity;

import com.emily.infrastructure.security.annotation.SecurityModel;
import com.emily.infrastructure.security.annotation.SecurityProperty;
import com.emily.infrastructure.test.security.plugin.AddressEncryptionPlugin;

/**
 * @author :  Emily
 * @since :  2025/2/8 下午5:43
 */
@SecurityModel
public class Address {
    @SecurityProperty(value = AddressEncryptionPlugin.class)
    private String city;
    @SecurityProperty(value = AddressEncryptionPlugin.class)
    private String country;
    private long height;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }
}
