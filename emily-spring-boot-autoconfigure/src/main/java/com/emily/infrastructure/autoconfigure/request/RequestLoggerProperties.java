package com.emily.infrastructure.autoconfigure.request;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;

/**
 * @description: 拦截器属性配置类
 * @create: 2020/03/19
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@ConfigurationProperties(prefix = "spring.emily.request.logback")
public class RequestLoggerProperties {
    /**
     * 组件开关
     */
    private boolean enabled;
    /**
     * 是否开启debug模式
     */
    private boolean debug;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
