package com.emily.infrastructure.redis.example.entity;

import java.text.MessageFormat;
import java.util.Properties;

/**
 * Redis监控指标
 *
 * @author Emily
 * @since 2021/09/16
 */
public class RedisIndicator {
    /**
     * 客户端连接数
     */
    private String connectedClients;

    public String getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(String connectedClients) {
        this.connectedClients = connectedClients;
    }

    public static String toString(Properties properties) {
        StringBuffer sb = new StringBuffer();
        sb.append(MessageFormat.format("客户端连接数：{0}", properties.getProperty("connected_clients")));
        return sb.toString();
    }
}
