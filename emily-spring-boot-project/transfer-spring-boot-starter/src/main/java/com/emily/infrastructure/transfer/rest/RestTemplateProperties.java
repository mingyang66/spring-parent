package com.emily.infrastructure.transfer.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 配置元数据文件.
 *
 * @author Emily
 */
@ConfigurationProperties(prefix = RestTemplateProperties.PREFIX)
public class RestTemplateProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.rest-template";
    /**
     * 组件开关
     */
    private boolean enabled;
    /**
     * HttpClientService read timeout (in milliseconds),default:5000
     */
    private Duration readTimeOut = Duration.ofMillis(5000);
    /**
     * HttpClientService connect timeout (in milliseconds),default:10000
     */
    private Duration connectTimeOut = Duration.ofMillis(10000);
    /**
     * 是否开启SSL，默认：true
     */
    private boolean ssl = false;

    public Duration getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(Duration readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public Duration getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(Duration connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}
