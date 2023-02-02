package com.emily.infrastructure.autoconfigure.route;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description :  属性配置
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/2 11:07 上午
 */
@ConfigurationProperties(prefix = LookupPathProperties.PREFIX)
public class LookupPathProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.route";
    /**
     * 属性配置开关，默认：false
     */
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
