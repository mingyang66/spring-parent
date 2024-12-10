package com.emily.sample.desensitize.entity;

import com.emily.infrastructure.sensitive.DesensitizeType;
import com.emily.infrastructure.sensitive.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    /**
     * {@link DesensitizeProperty}注解和{@link DesensitizeMapProperty} 注解都可以对Map集合中value为String的值进行脱敏处理；
     * {@link DesensitizeMapProperty}注解优先级高于{@link DesensitizeProperty}注解
     */
    @DesensitizeProperty
    @DesensitizeMapProperty(keys = {"password", "username"}, types = {DesensitizeType.DEFAULT, DesensitizeType.USERNAME})
    private Map<String, Object> dataMap = new HashMap<>();
    @DesensitizeProperty
    private List<String> list;
    @DesensitizeProperty
    private String[] arrays;
    /**
     * 将任何引用类型字段设置为null,且优先级最高
     */
    @DesensitizeNullProperty
    private Double testNull;
    /**
     * 复杂字段脱敏处理，根据传入的字段key值判断对应字段value是否进行脱敏处理
     */
    @DesensitizeComplexProperty(keys = {"email", "phone"}, value = "fieldValue", types = {DesensitizeType.EMAIL, DesensitizeType.PHONE})
    private String fieldKey;
    private String fieldValue;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String[] getArrays() {
        return arrays;
    }

    public void setArrays(String[] arrays) {
        this.arrays = arrays;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Double getTestNull() {
        return testNull;
    }

    public void setTestNull(Double testNull) {
        this.testNull = testNull;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

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
