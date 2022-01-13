package com.emily.infrastructure.core.trace;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**m
 * @program: spring-parent
 * @description: 链路追踪上下文属性配置
 * @author: Emily
 * @create: 2021/11/27
 */
@ConfigurationProperties(prefix = TraceContextProperties.PREFIX)
public class TraceContextProperties {
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
