package com.emily.infrastructure.autoconfigure.request;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;

/**
 * @author Emily
 * @description: 拦截器属性配置类
 * @create: 2020/03/19
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@ConfigurationProperties(prefix = ApiRequestProperties.PREFIX)
public class ApiRequestProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.request";
    /**
     * 组件开关
     */
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
