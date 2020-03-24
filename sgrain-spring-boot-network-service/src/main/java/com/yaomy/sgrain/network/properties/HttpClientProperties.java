package com.yaomy.sgrain.network.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置元数据文件.
 */
@ConfigurationProperties(prefix = "spring.sgrain.http-client")
public class HttpClientProperties {
    /**
     * HttpClientService read timeout (in milliseconds),default:5000
     */
    private Integer readTimeOut = 5000;
    /**
     * HttpClientService connect timeout (in milliseconds),default:10000
     */
    private Integer connectTimeOut = 10000;

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
}
