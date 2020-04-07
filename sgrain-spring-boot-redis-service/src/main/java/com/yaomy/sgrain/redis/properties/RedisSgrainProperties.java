package com.yaomy.sgrain.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: Redis配置文件
 * @author: 姚明洋
 * @create: 2020/03/25
 */
@ConfigurationProperties(prefix = "spring.sgrain.redis")
public class RedisSgrainProperties {
    /**
     * 组件开关
     */
    private Boolean enable = Boolean.TRUE;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
