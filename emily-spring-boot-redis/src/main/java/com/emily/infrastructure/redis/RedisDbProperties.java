package com.emily.infrastructure.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis多数据源配置文件
 *
 * @author Emily
 * @since 2021/07/11
 */
@ConfigurationProperties(prefix = RedisDbProperties.PREFIX)
public class RedisDbProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.redis";
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 是否开启客户端监控
     */
    private boolean monitorEnabled = false;
    /**
     * 监控Redis数据库固定间隔时间，默认：30s
     */
    private Duration monitorFireRate = Duration.ofSeconds(30);
    /**
     * 是否开启共享本地物理连接校验，默认：false
     * 如果校验失败，则新建连接
     * 开启后会损耗部分性能，每次获取连接都要校验是否开启及调用ping方法
     */
    private boolean validateConnection = false;
    /**
     * 是否开启共享本地物理连接，默认：true
     */
    private boolean shareNativeConnection = true;
    /**
     * 默认配置
     */
    private String defaultConfig = "default";
    /**
     * 客户端类型
     */
    private RedisProperties.ClientType clientType = RedisProperties.ClientType.LETTUCE;
    /**
     * 多数据源配置
     */
    private Map<String, RedisProperties> config = new HashMap<>();

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

    public void setConfig(Map<String, RedisProperties> config) {
        this.config = config;
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

    public Duration getMonitorFireRate() {
        return monitorFireRate;
    }

    public void setMonitorFireRate(Duration monitorFireRate) {
        this.monitorFireRate = monitorFireRate;
    }

    public boolean isMonitorEnabled() {
        return monitorEnabled;
    }

    public void setMonitorEnabled(boolean monitorEnabled) {
        this.monitorEnabled = monitorEnabled;
    }

    public boolean isValidateConnection() {
        return validateConnection;
    }

    public void setValidateConnection(boolean validateConnection) {
        this.validateConnection = validateConnection;
    }

    public boolean isShareNativeConnection() {
        return shareNativeConnection;
    }

    public void setShareNativeConnection(boolean shareNativeConnection) {
        this.shareNativeConnection = shareNativeConnection;
    }
}
