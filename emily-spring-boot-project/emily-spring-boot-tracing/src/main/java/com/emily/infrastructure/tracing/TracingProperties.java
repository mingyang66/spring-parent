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
     * 系统编号
     */
    private String systemNumber;

    public String getSystemNumber() {
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
    }
}
