package com.emily.infrastructure.test.po.json;

import com.emily.infrastructure.common.sensitive.JsonSensitive;
import com.emily.infrastructure.common.sensitive.SensitiveType;

import java.util.Map;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2022/10/27 10:53 上午
 */
public class PubRequest {
    @JsonSensitive(SensitiveType.USERNAME)
    public String username;
    @JsonSensitive
    public String password;
    @JsonSensitive(SensitiveType.EMAIL)
    public String email;
    @JsonSensitive(SensitiveType.ID_CARD)
    public String idCard;
    @JsonSensitive(SensitiveType.BANK_CARD)
    public String bankCard;
    @JsonSensitive(SensitiveType.PHONE)
    public String phone;
    @JsonSensitive(SensitiveType.PHONE)
    public String mobile;
    public Job job;
    public Map<String, Object> work;


    public static class Job {
        @JsonSensitive(SensitiveType.DEFAULT)
        public String work;
        @JsonSensitive(SensitiveType.EMAIL)
        public String email;

    }
}
