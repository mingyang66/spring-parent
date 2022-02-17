package com.emily.cloud.test.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: http属性配置文件
 * @create: 2020/06/28
 */
@ConfigurationProperties(prefix = "server.http")
public class ServerProperties {
    /**
     * 是否开启http服务
     */
    private boolean enable;

    /**
     * 端口号
     */
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
