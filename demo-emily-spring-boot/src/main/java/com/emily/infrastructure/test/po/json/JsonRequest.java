package com.emily.infrastructure.test.po.json;


import com.emily.infrastructure.sensitive.SensitiveType;
import com.emily.infrastructure.sensitive.annotation.JsonFlexField;
import com.emily.infrastructure.sensitive.annotation.JsonSensitive;
import com.emily.infrastructure.sensitive.annotation.JsonSimField;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Emily
 * @since Created in 2022/10/27 10:53 上午
 */
@JsonSensitive
public class JsonRequest extends Animal {
    @JsonFlexField(keys = {"email", "phone"}, value = "fieldValue", types = {SensitiveType.EMAIL, SensitiveType.PHONE})
    private String fieldKey;
    private String fieldValue;
    @JsonFlexField(keys = {"email", "phone"}, value = "fieldValue1")
    private String fieldKey1;
    private String fieldValue1;
    @NotEmpty
    @JsonSimField(SensitiveType.USERNAME)
    private String username;
    @JsonSimField
    private String password;
    @JsonSimField(SensitiveType.EMAIL)
    private String email;
    @JsonSimField(SensitiveType.ID_CARD)
    private String idCard;
    @JsonSimField(SensitiveType.BANK_CARD)
    private String bankCard;
    @JsonSimField(SensitiveType.PHONE)
    private String phone;
    @JsonSimField(SensitiveType.PHONE)
    private String mobile;
    private int zs;
    private char c = 1;
    private BigDecimal d = new BigDecimal(4);
    private Job job;
    @JsonSimField
    private Map<String, Object> work;

    public String getFieldKey1() {
        return fieldKey1;
    }

    public void setFieldKey1(String fieldKey1) {
        this.fieldKey1 = fieldKey1;
    }

    public String getFieldValue1() {
        return fieldValue1;
    }

    public void setFieldValue1(String fieldValue1) {
        this.fieldValue1 = fieldValue1;
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

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public int getZs() {
        return zs;
    }

    public void setZs(int zs) {
        this.zs = zs;
    }

    public BigDecimal getD() {
        return d;
    }

    public void setD(BigDecimal d) {
        this.d = d;
    }

    public Map<String, Object> getWork() {
        return work;
    }

    public void setWork(Map<String, Object> work) {
        this.work = work;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    @JsonSensitive
    public static class Job {
        @JsonSimField(SensitiveType.DEFAULT)
        private String work;
        @JsonSimField(SensitiveType.EMAIL)
        private String email;
        @JsonFlexField(keys = {"email", "phone"}, value = "fieldValue", types = {SensitiveType.EMAIL, SensitiveType.PHONE})
        private String fieldKey;
        private String fieldValue;

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

        public String getWork() {
            return work;
        }

        public void setWork(String work) {
            this.work = work;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
