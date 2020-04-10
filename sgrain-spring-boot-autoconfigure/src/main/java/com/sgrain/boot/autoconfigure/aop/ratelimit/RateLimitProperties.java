package com.sgrain.boot.autoconfigure.aop.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: 流控属性绑定配置文件件类
 * @create: 2020/03/25
 */
@ConfigurationProperties(prefix = "spring.sgrain.rate-limit")
public class RateLimitProperties {
    /**
     * 组件开关
     */
    private Boolean enable;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
