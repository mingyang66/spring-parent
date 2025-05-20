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

    /**
     * 判断是否开启加解密组件
     *
     * @return true-是，false-否
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否开启加解密组件
     *
     * @param enabled 是否开启
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
