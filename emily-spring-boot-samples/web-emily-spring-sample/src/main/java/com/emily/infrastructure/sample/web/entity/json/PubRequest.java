package com.emily.infrastructure.sample.web.entity.json;


import com.emily.infrastructure.sensitive.DesensitizeType;
import com.emily.infrastructure.sensitive.annotation.DesensitizeModel;
import com.emily.infrastructure.sensitive.annotation.DesensitizeProperty;

import java.util.List;
import java.util.Map;

/**
 * @author Emily
 * @since Created in 2022/10/27 10:53 上午
 */
@DesensitizeModel
public class PubRequest {
    @DesensitizeProperty(DesensitizeType.USERNAME)
    public String username;
    @DesensitizeProperty
    public String password;
    @DesensitizeProperty(DesensitizeType.EMAIL)
    public String email;
    @DesensitizeProperty(DesensitizeType.ID_CARD)
    public String idCard;
    @DesensitizeProperty(DesensitizeType.BANK_CARD)
    public String bankCard;
    @DesensitizeProperty(DesensitizeType.PHONE)
    public String phone;
    @DesensitizeProperty(DesensitizeType.PHONE)
    public String mobile;
    @DesensitizeProperty
    public Job job;
    @DesensitizeProperty
    public Map<String, Object> work;
    @DesensitizeProperty
    public List<String> jobList;
    @DesensitizeProperty
    public String[] jobs;


    @DesensitizeModel
    public static class Job {
        @DesensitizeProperty(DesensitizeType.DEFAULT)
        public String work;
        @DesensitizeProperty(DesensitizeType.EMAIL)
        public String email;

    }
}
