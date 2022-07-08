package com.emily.infrastructure.autoconfigure.exception;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 异常处理自动化配置PO
 * @create: 2020/10/28
 */
@ConfigurationProperties(prefix = GlobalExceptionProperties.PREFIX)
public class GlobalExceptionProperties {
    /**
     * 配置前缀
     */
    public static final String PREFIX = "spring.emily.exception";
    /**
     * 是否开启异常拦截
     */
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
