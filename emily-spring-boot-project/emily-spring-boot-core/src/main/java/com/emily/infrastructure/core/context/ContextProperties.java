package com.emily.infrastructure.core.context;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 链路追踪上下文属性配置
 *
 * @author Emily
 * @since 2021/11/27
 */
@ConfigurationProperties(prefix = ContextProperties.PREFIX)
public class ContextProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.context";
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
