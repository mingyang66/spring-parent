package com.emily.infrastructure.autoconfigure.tomcat;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Emily
 * @description: http属性配置文件
 * @create: 2020/06/28
 */
@ConfigurationProperties(prefix = "server.http")
public class TomcatProperties {
    /**
     * 是否开启http服务
     */
    private boolean enabled;
    /**
     * 端口号
     */
    private int port = 8080;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
