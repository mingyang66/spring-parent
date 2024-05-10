package com.emily.infrastructure.sensitive.test.entity;


import com.emily.infrastructure.sensitive.SensitiveType;
import com.emily.infrastructure.sensitive.annotation.JsonSensitive;
import com.emily.infrastructure.sensitive.annotation.JsonSimField;

import java.util.List;
import java.util.Map;

/**
 * @author Emily
 * @since Created in 2022/10/27 10:53 上午
 */
@JsonSensitive
public class PubResponse {
    @JsonSimField(SensitiveType.USERNAME)
    public String username;
    @JsonSimField
    public String password;
    @JsonSimField(SensitiveType.EMAIL)
    public String email;
    @JsonSimField(SensitiveType.ID_CARD)
    public String idCard;
    @JsonSimField(SensitiveType.BANK_CARD)
    public String bankCard;
    @JsonSimField(SensitiveType.PHONE)
    public String phone;
    @JsonSimField(SensitiveType.PHONE)
    public String mobile;
    public Job job;
    public Map<String, Object> work;
    public List<Job> jobList;
    public Job[] jobs;

    @JsonSensitive
    public static class Job {
        @JsonSimField(SensitiveType.DEFAULT)
        public String work;
        @JsonSimField(SensitiveType.EMAIL)
        public String email;

    }
}
