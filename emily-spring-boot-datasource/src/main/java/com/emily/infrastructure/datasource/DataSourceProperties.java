package com.emily.infrastructure.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 数据源配置文件
 * @author: Emily
 * @create: 2020/05/14
 */
@ConfigurationProperties(prefix = DataSourceProperties.PREFIX)
public class DataSourceProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.datasource";
    /**
     * 默认数据源配置，默认：default
     */
    public static final String DEFAULT_CONFIG = "default";
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 默认数据源配置，默认：default
     */
    private String defaultConfig = DEFAULT_CONFIG;
    /**
     * 多数据源配置
     */
    private Map<String, DruidDataSource> config = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Map<String, DruidDataSource> getConfig() {
        return config;
    }

    public void setConfig(Map<String, DruidDataSource> config) {
        this.config = config;
    }

    public DruidDataSource getDefaultDataSource() {
        return this.config.get(this.getDefaultConfig());
    }
}
