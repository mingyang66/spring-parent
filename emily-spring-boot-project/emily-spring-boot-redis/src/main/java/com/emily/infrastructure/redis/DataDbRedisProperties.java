package com.emily.infrastructure.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis多数据源配置文件
 *
 * @author Emily
 * @since 2021/07/11
 */
@ConfigurationProperties(prefix = DataDbRedisProperties.PREFIX)
public class DataDbRedisProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.redis";
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 是否开启容器监听器功能，默认：false
     */
    private boolean listener;
    /**
     * 默认配置标识
     */
    private String defaultConfig;
    /**
     * 客户端类型
     */
    private RedisProperties.ClientType clientType = RedisProperties.ClientType.LETTUCE;

    /**
     * 多数据源配置
     */
    private final Map<String, RedisProperties> config = new HashMap<>();

    public Map<String, RedisProperties> getConfig() {
        return config;
    }

    public RedisProperties.ClientType getClientType() {
        return clientType;
    }

    public void setClientType(RedisProperties.ClientType clientType) {
        this.clientType = clientType;
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RedisProperties getDefaultDataSource() {
        return this.config.get(this.getDefaultConfig());
    }

    public boolean isListener() {
        return listener;
    }

    public void setListener(boolean listener) {
        this.listener = listener;
    }
}
