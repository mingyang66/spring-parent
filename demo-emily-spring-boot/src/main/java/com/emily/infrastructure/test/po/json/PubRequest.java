package com.emily.infrastructure.test.po.json;

import com.emily.infrastructure.common.sensitive.JsonIgnore;
import com.emily.infrastructure.common.sensitive.SensitiveType;

import java.util.Map;

/**
 * @Description :
 * @Author :  姚明洋
 * @CreateDate :  Created in 2022/10/27 10:53 上午
 */
public class PubRequest {
    @JsonIgnore(SensitiveType.USERNAME)
    public String username;
    @JsonIgnore
    public String password;
    @JsonIgnore(SensitiveType.EMAIL)
    public String email;
    @JsonIgnore(SensitiveType.ID_CARD)
    public String idCard;
    @JsonIgnore(SensitiveType.BANK_CARD)
    public String bankCard;
    @JsonIgnore(SensitiveType.MOBILE_PHONE)
    public String phone;
    @JsonIgnore(SensitiveType.FIXED_PHONE)
    public String mobile;
    public Job job;
    public Map<String, Object> work;


    public static class Job {
        @JsonIgnore(SensitiveType.DEFAULT)
        public String work;
        @JsonIgnore(SensitiveType.EMAIL)
        public String email;

    }
}
