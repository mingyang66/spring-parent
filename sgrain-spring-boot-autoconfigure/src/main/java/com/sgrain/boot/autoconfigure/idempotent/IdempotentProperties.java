package com.sgrain.boot.autoconfigure.idempotent;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: 重复提交属性配置类
 * @create: 2020/03/26
 */
@ConfigurationProperties(prefix = "spring.sgrain.idempotent")
public class IdempotentProperties {
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
