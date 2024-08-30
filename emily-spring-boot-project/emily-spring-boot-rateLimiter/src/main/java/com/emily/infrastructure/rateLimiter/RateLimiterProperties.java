package com.emily.infrastructure.rateLimiter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author :  姚明洋
 * @since :  2024/8/29 下午5:45
 */
@ConfigurationProperties(prefix = RateLimiterProperties.PREFIX)
public class RateLimiterProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.threshold";
    /**
     * 是否开启限流组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 是否拦截超类或者接口中的方法，默认：true
     */
    private boolean checkInherited = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCheckInherited() {
        return checkInherited;
    }

    public void setCheckInherited(boolean checkInherited) {
        this.checkInherited = checkInherited;
    }
}
