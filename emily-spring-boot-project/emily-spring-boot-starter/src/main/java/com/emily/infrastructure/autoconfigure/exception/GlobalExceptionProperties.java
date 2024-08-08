package com.emily.infrastructure.autoconfigure.exception;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异常处理自动化配置PO
 *
 * @author Emily
 * @since 2020/10/28
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
