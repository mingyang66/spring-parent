package com.emily.infrastructure.autoconfigure.httpclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置元数据文件.
 */
@ConfigurationProperties(prefix = HttpClientProperties.PREFIX)
public class HttpClientProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.http-client";
    /**
     * 组件开关
     */
    private boolean enabled;
    /**
     * HttpClientService read timeout (in milliseconds),default:5000
     */
    private Integer readTimeOut = 5000;
    /**
     * HttpClientService connect timeout (in milliseconds),default:10000
     */
    private Integer connectTimeOut = 10000;
    /**
     * 开启调用接口拦截器
     */
    private boolean interceptor = true;

    public Integer getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(Integer readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public Integer getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(Integer connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInterceptor() {
        return interceptor;
    }

    public void setInterceptor(boolean interceptor) {
        this.interceptor = interceptor;
    }
}
