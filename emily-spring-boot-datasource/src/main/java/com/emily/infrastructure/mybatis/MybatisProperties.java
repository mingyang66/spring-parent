package com.emily.infrastructure.mybatis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据源配置文件
 *
 * @author : Emily
 * @since : 2020/05/14
 */
@ConfigurationProperties(prefix = MybatisProperties.PREFIX)
public class MybatisProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.mybatis";
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 是否拦截超类或者接口中的方法，默认：true
     */
    private boolean checkInherited = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCheckInherited() {
        return checkInherited;
    }

    public void setCheckInherited(boolean checkInherited) {
        this.checkInherited = checkInherited;
    }
}
