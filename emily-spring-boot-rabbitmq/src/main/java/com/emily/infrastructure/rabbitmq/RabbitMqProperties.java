package com.emily.infrastructure.rabbitmq;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description :  RabbitMq属性配置
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/2 5:06 下午
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
     * RabbitMq属性配置
     */
    private Map<String, RabbitProperties> config = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, RabbitProperties> getConfig() {
        return config;
    }

    public void setConfig(Map<String, RabbitProperties> config) {
        this.config = config;
    }
}
