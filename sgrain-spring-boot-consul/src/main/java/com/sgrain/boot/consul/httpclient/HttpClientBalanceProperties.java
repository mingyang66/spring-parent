package com.sgrain.boot.consul.httpclient;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置元数据文件.
 */
@ConfigurationProperties(prefix = "spring.sgrain.cloud.http-client-balance")
public class HttpClientBalanceProperties {
    /**
     * 组件开关
     */
    private boolean enable;
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
    private boolean enableInterceptor = true;

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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnableInterceptor() {
        return enableInterceptor;
    }

    public void setEnableInterceptor(boolean enableInterceptor) {
        this.enableInterceptor = enableInterceptor;
    }
}
