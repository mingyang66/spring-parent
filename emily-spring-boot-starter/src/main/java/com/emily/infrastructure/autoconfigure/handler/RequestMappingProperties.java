package com.emily.infrastructure.autoconfigure.handler;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description :  属性配置
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/2 11:07 上午
 */
@ConfigurationProperties(prefix = RequestMappingProperties.PREFIX)
public class RequestMappingProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.mapping";
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
