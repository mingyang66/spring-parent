package com.emily.infrastructure.rpc.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: RPC客户端属性配置类
 * @author: 姚明洋
 * @create: 2021/09/22
 */
@ConfigurationProperties(RpcClientProperties.PREFIX)
public class RpcClientProperties {
    /**
     * RPC属性配置前缀
     */
    public static final String PREFIX = "spring.emily.rpc.client";
    /**
     * 是否开启RPC客户端配置类
     */
    private boolean enabled = true;
    /**
     * RPC服务器host地址，默认：127.0.0.1
     */
    private String host = "127.0.0.1";
    /**
     * RPC服务器端口号，默认：9999
     */
    private int port = 9999;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
