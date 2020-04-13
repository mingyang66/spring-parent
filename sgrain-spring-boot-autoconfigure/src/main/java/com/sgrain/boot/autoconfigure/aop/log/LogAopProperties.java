package com.sgrain.boot.autoconfigure.aop.log;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: 拦截器属性配置类
 * @create: 2020/03/19
 */
@ConfigurationProperties(prefix = "spring.sgrain.log-aop")
public class LogAopProperties {
    /**
     * 组件开关
     */
    private Boolean enable;
    private Boolean debug;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }
}
