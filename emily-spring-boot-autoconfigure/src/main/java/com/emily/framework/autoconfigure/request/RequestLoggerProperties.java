package com.emily.framework.autoconfigure.request;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: 拦截器属性配置类
 * @create: 2020/03/19
 */
@ConfigurationProperties(prefix = "spring.emily.request.logger")
public class RequestLoggerProperties {
    /**
     * 组件开关
     */
    private boolean enabled;
    /**
     * 是否开启debug模式
     */
    private boolean debug;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
