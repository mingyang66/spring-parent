package com.emily.infrastructure.desensitize;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author :  Emily
 * @since :  2024/12/7 下午3:50
 */
@ConfigurationProperties(prefix = DesensitizeProperties.PREFIX)
public class DesensitizeProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.desensitize";
    /**
     * 是否开启脱敏组件, 默认：true
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
