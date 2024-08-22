package com.emily.infrastructure.transfer.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 拦截器属性配置类
 *
 * @author Emily
 * @since 2020/03/19
 */
@ConfigurationProperties(prefix = FeignProperties.PREFIX)
public class FeignProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.transfer.feign";
    /**
     * 组件开关
     */
    private boolean enabled;


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
