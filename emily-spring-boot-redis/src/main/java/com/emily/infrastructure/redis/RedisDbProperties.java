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
     * 默认配置标识
     */
    private String defaultConfig;
    /**
     * 客户端类型
     */
    private RedisProperties.ClientType clientType = RedisProperties.ClientType.LETTUCE;
    /**
     * 基于lettuce-core的客户端属性配置
     */
    private Lettuce lettuce = new Lettuce();
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

    public Lettuce getLettuce() {
        return lettuce;
    }

    public void setLettuce(Lettuce lettuce) {
        this.lettuce = lettuce;
    }

    public RedisProperties getDefaultDataSource() {
        return this.config.get(this.getDefaultConfig());
    }

    public static class Lettuce {
        /**
         * 是否开启连接校验，默认：false
         */
        private boolean validateConnection = false;
        /**
         * 是否开启共享本地物理连接，默认：true
         */
        private boolean shareNativeConnection = true;
        /**
         * 是否提前初始化连接，默认：false
         * 属性shareNativeConnection为true时才生效
         */
        private boolean eagerInitialization = false;
        /**
         * 对象在池中最小可空闲时间的属性
         * 它指定了一个对象在池中保持空闲的最小时间，超过这个时间后，如果池中的对象数量超过了BaseObjectPoolConfig.minIdle设置的最小空闲对象数量，就会触发空闲对象的逐出操作
         */
        private Duration minEvictableIdleDuration = Duration.ofMinutes(30);

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

        public boolean isEagerInitialization() {
            return eagerInitialization;
        }

        public void setEagerInitialization(boolean eagerInitialization) {
            this.eagerInitialization = eagerInitialization;
        }

        public Duration getMinEvictableIdleDuration() {
            return minEvictableIdleDuration;
        }

        public void setMinEvictableIdleDuration(Duration minEvictableIdleDuration) {
            this.minEvictableIdleDuration = minEvictableIdleDuration;
        }
    }
}
