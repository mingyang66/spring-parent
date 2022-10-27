package com.emily.infrastructure.test.po.json;

import com.emily.infrastructure.common.sensitive.SensitiveType;
import com.emily.infrastructure.common.sensitive.annotation.JsonIgnore;

import java.util.Map;

/**
 * @Description :
 * @Author :  姚明洋
 * @CreateDate :  Created in 2022/10/27 10:53 上午
 */
public class JsonResponse {
    @JsonIgnore(type = SensitiveType.USERNAME)
    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore(type = SensitiveType.EMAIL)
    private String email;
    @JsonIgnore(type = SensitiveType.ID_CARD)
    private String idCard;
    @JsonIgnore(type = SensitiveType.BANK_CARD)
    private String bankCard;
    @JsonIgnore(type = SensitiveType.MOBILE_PHONE)
    private String phone;
    @JsonIgnore(type = SensitiveType.FIXED_PHONE)
    private String mobile;
    private JsonRequest.Job job;
    private Map<String, Object> work;

    public Map<String, Object> getWork() {
        return work;
    }

    public void setWork(Map<String, Object> work) {
        this.work = work;
    }

    public JsonRequest.Job getJob() {
        return job;
    }

    public void setJob(JsonRequest.Job job) {
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

    public static class Job {
        @JsonIgnore(type = SensitiveType.DEFAULT)
        private String work;
        @JsonIgnore(type = SensitiveType.EMAIL)
        private String email;

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
