package com.emily.infrastructure.i18n;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author :  Emily
 * @since :  2024/10/31 上午10:12
 */
@ConfigurationProperties(prefix = I18nProperties.PREFIX)
public class I18nProperties {

    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.i18n";
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
