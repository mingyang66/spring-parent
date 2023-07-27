package com.emily.infrastructure.rabbitmq;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * RabbitMq属性配置
 *
 * @author Emily
 * @since Created in 2022/6/2 5:06 下午
 */
@ConfigurationProperties(prefix = RabbitMqProperties.PREFIX)
public class RabbitMqProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.rabbitmq";
    /**
     * 是否开启RabbitMq组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 默认配置
     */
    private String defaultConfig;
    /**
     * RabbitMq属性配置
     */
    private Map<String, RabbitProperties> config;

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

    public Map<String, RabbitProperties> getConfig() {
        return config;
    }

    public void setConfig(Map<String, RabbitProperties> config) {
        this.config = config;
    }
}
