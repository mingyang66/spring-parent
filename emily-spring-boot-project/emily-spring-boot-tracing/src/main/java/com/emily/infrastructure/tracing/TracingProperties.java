package com.emily.infrastructure.tracing;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 链路追踪上下文属性配置
 *
 * @author Emily
 * @since 2021/11/27
 */
@ConfigurationProperties(prefix = TracingProperties.PREFIX)
public class TracingProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.tracing";
    /**
     * 组件开关，默认：true
     */
    private boolean enabled = true;
    /**
     * 系统编号
     */
    private String systemNumber;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSystemNumber() {
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
    }
}
