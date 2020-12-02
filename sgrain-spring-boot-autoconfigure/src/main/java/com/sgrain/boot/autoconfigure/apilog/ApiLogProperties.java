package com.sgrain.boot.autoconfigure.apilog;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: 拦截器属性配置类
 * @create: 2020/03/19
 */
@ConfigurationProperties(prefix = "spring.sgrain.api-log")
public class ApiLogProperties {
    /**
     * 组件开关
     */
    private boolean enable;
    /**
     * 是否开启debug模式
     */
    private boolean debug;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
