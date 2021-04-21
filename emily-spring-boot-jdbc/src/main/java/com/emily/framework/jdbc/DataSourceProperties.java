package com.emily.framework.jdbc;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: 数据源配置文件
 * @author: Emily
 * @create: 2020/05/14
 */
@ConfigurationProperties(prefix = "spring.emily.jdbc.datasource")
public class DataSourceProperties {
    /**
     * 是否开启数据源组件
     */
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
