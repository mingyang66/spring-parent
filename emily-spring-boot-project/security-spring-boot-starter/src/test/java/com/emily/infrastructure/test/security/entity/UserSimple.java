package com.emily.infrastructure.test.security.entity;

import com.emily.infrastructure.security.annotation.SecurityModel;
import com.emily.infrastructure.security.annotation.SecurityProperty;
import com.emily.infrastructure.test.security.plugin.SimpleEncryptionPlugin;
import com.emily.infrastructure.test.security.plugin.UserSimpleEncryptionPlugin;

import java.util.List;
import java.util.Map;

/**
 * @author :  Emily
 * @since :  2025/2/8 下午4:32
 */
@SecurityModel
public class UserSimple {
    @SecurityProperty(SimpleEncryptionPlugin.class)
    private String simple;
    @SecurityProperty(value = UserSimpleEncryptionPlugin.class)
    private String username;
    @SecurityProperty(value = UserSimpleEncryptionPlugin.class)
    private String password;
    private int age;
    private Address address;
    @SecurityProperty(value = UserSimpleEncryptionPlugin.class)
    private Map<String, String> strMap;
    private Map<String, Address> addressMap;
    @SecurityProperty(value = UserSimpleEncryptionPlugin.class)
    private List<String> list;
    private List<Address> addressList;

    public String getSimple() {
        return simple;
    }

    public void setSimple(String simple) {
        this.simple = simple;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public Map<String, String> getStrMap() {
        return strMap;
    }

    public void setStrMap(Map<String, String> strMap) {
        this.strMap = strMap;
    }

    public Map<String, Address> getAddressMap() {
        return addressMap;
    }

    public void setAddressMap(Map<String, Address> addressMap) {
        this.addressMap = addressMap;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
