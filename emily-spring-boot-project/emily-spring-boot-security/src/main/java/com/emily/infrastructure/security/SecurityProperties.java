package com.emily.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 入参加解密配置类
 *
 * @author :  Emily
 * @since :  2024/10/31 上午10:12
 */
@ConfigurationProperties(prefix = SecurityProperties.PREFIX)
public class SecurityProperties {

    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emis.security";
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
