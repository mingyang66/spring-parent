package com.emily.infrastructure.test.po.json;

import com.emily.infrastructure.sensitive.JsonSensitive;
import com.emily.infrastructure.sensitive.JsonSimField;
import com.emily.infrastructure.sensitive.SensitiveType;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author  Emily
 * @since  Created in 2022/10/27 10:53 上午
 */
@JsonSensitive
public class JsonResponse {
    private int a;
    private byte[] b;
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
    private Job job;
    private Job[] jobs;
    private String[] arr;
    private Set<Job> list;
    private String dateFormat;
    @JsonSimField
    private Map<String, Object> work;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public byte[] getB() {
        return b;
    }

    public void setB(byte[] b) {
        this.b = b;
    }

    public String[] getArr() {
        return arr;
    }

    public void setArr(String[] arr) {
        this.arr = arr;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Job[] getJobs() {
        return jobs;
    }

    public void setJobs(Job[] jobs) {
        this.jobs = jobs;
    }

    public Set<Job> getList() {
        return list;
    }

    public void setList(Set<Job> list) {
        this.list = list;
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
