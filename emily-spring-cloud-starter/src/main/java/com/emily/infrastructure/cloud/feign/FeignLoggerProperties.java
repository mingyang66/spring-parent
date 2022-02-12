package com.emily.infrastructure.cloud.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Emily
 * @description: 拦截器属性配置类
 * @create: 2020/03/19
 */
@ConfigurationProperties(prefix = FeignLoggerProperties.PREFIX)
public class FeignLoggerProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.feign.logger";
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
