package com.yaomy.security.oauth2.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @Description: 获取配置文件中配置属性类
 * @ProjectName: EM.FrontEnd.PrivateEquity.electronic-contract
 * @Package: com.uufund.ecapi.common.PropertyService
 * @Author: 姚明洋
 * @Date: 2019/6/11 14:31
 * @Version: 1.0
 */
@Service
public class PropertyService {
    @Autowired
    private Environment env;
    /**
     * @Description 获取配置文件中指定的属性值
     * @Author 姚明洋
     * @Date 2019/6/11 14:47
     * @Version  1.0
     */
    public String getProperty(String property){
        return env.getProperty(property);
    }
    /**
     * @Description 获取配置文件的属性，如果为空，则为默认值
     * @Author 姚明洋
     * @Date 2019/6/19 14:01
     * @Version  1.0
     */
    public String getProperty(String property, String defaultValue){
        String val = env.getProperty(property);
        if(StringUtils.isEmpty(val)){
            return  defaultValue;
        }
        return val;
    }
    /**
     * @Description 获取配置文件中指定的属性值
     * @Author 姚明洋
     * @Date 2019/6/11 14:37
     * @Version  1.0
     */
    public <T> T getProperty(String property, Class<T> clazz){
        return env.getProperty(property, clazz);
    }
    /**
     * @Description 获取配置文件中指定的属性，属性为null,则返回默认defaultValue
     * @Author 姚明洋
     * @Date 2019/6/19 14:06
     * @Version  1.0
     */
    public <T> T getProperty(String property, T defaultValue, Class<T> clazz){
        T val = env.getProperty(property, clazz);
        if(val == null){
            return defaultValue;
        }
        return val;
    }
    /**
     * @Description 判断配置文件中是否存在指定的key值
     * @Author 姚明洋
     * @Date 2019/6/11 14:36
     * @Version  1.0
     */
    public boolean containsProperty(String property){
        return env.containsProperty(property);
    }
}
