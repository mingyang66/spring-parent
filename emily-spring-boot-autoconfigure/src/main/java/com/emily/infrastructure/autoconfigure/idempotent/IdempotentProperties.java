package com.emily.infrastructure.autoconfigure.idempotent;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: 重复提交属性配置类
 * @create: 2020/03/26
 */
@ConfigurationProperties(prefix = "spring.emily.idempotent")
public class IdempotentProperties {
    /**
     * 防止重复提交组件
     */
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
