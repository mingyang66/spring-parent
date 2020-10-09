package com.sgrain.boot.quartz.config;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/09/29
 */
//@ConfigurationProperties(prefix = "spring.sgrain.test")
public class TestProperties {
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
