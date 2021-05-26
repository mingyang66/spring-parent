package com.emily.infrastructure.autoconfigure.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: 流控属性绑定配置文件件类
 * @create: 2020/03/25
 */
@ConfigurationProperties(prefix = "spring.emily.rate-limit")
public class RateLimitProperties {
    /**
     * 限流组件开关
     */
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
