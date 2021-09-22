package com.emily.infrastructure.rpc.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: RPC服务端配置
 * @author: Emily
 * @create: 2021/09/22
 */
@ConfigurationProperties(prefix = RpcServerProperties.PREFIX)
public class RpcServerProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.rpc.server";
    /**
     * 是否开启RPC服务端
     */
    private boolean enabled = true;
    /**
     * 端口号,默认：9999
     */
    private int port = 9999;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
