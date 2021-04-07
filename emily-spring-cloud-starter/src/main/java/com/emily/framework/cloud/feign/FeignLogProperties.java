package com.emily.framework.cloud.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: 拦截器属性配置类
 * @create: 2020/03/19
 */
@ConfigurationProperties(prefix = "spring.emily.feign.http-log")
public class FeignLogProperties {
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
