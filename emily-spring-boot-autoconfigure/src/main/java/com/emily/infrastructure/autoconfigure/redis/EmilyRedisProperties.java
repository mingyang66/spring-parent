package com.emily.infrastructure.autoconfigure.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: Redis配置文件
 * @create: 2020/03/25
 */
@ConfigurationProperties(prefix = "spring.emily.redis")
public class EmilyRedisProperties {
    /**
     * 组件开关
     */
    private boolean enable;


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
