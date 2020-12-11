package com.emily.boot.autoconfigure.converters;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: jackson转换器配置类
 * @create: 2020/10/28
 */
@ConfigurationProperties(prefix = "spring.emily.jackson2.converter")
public class Jackson2MessagesProperties {
    /**
     * 是否开启json转换器配置
     */
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
