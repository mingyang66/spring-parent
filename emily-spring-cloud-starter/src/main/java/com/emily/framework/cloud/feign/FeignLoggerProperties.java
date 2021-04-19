package com.emily.framework.cloud.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: 拦截器属性配置类
 * @create: 2020/03/19
 */
@ConfigurationProperties(prefix = "spring.emily.feign.logger")
public class FeignLoggerProperties {
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
