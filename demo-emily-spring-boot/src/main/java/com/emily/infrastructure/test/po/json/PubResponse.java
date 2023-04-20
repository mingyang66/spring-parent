package com.emily.infrastructure.test.po.json;

import com.emily.infrastructure.common.sensitive.JsonSensitive;
import com.emily.infrastructure.common.sensitive.JsonSimField;
import com.emily.infrastructure.common.sensitive.SensitiveType;

import java.util.List;
import java.util.Map;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2022/10/27 10:53 上午
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
    public List<PubResponse.Job> jobList;
    public PubResponse.Job[] jobs;

    @JsonSensitive
    public static class Job {
        @JsonSimField(SensitiveType.DEFAULT)
        public String work;
        @JsonSimField(SensitiveType.EMAIL)
        public String email;

    }
}
