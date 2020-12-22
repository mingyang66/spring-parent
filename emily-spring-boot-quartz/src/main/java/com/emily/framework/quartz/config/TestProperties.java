package com.emily.framework.quartz.config;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/09/29
 */
//@ConfigurationProperties(prefix = "spring.emily.test")
public class TestProperties {
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
