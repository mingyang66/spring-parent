package com.sgrain.boot.web.conf.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Description: 获取配置文件中配置属性类
 * @Version: 1.0
 */
@Component
public class PropertyService {

    private Environment env;

    public PropertyService(Environment env){
        this.env = env;
    }
    /**
     * @Description 获取配置文件中指定的属性值
     * @Version  1.0
     */
    public String getProperty(String property){
        return env.getProperty(property);
    }
    /**
     * @Description 获取配置文件的属性，如果为空，则为默认值
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
     * @Version  1.0
     */
    public <T> T getProperty(String property, Class<T> clazz){
        return env.getProperty(property, clazz);
    }
    /**
     * @Description 获取配置文件中指定的属性，属性为null,则返回默认defaultValue
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
     * @Version  1.0
     */
    public boolean containsProperty(String property){
        return env.containsProperty(property);
    }
}
