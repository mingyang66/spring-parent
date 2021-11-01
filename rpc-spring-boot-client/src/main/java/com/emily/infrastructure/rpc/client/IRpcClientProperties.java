package com.emily.infrastructure.rpc.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * @program: spring-parent
 * @description: RPC客户端属性配置类
 * @author: Emily
 * @create: 2021/09/22
 */
@ConfigurationProperties(IRpcClientProperties.PREFIX)
public class IRpcClientProperties {
    /**
     * RPC属性配置前缀
     */
    public static final String PREFIX = "spring.emily.rpc.client";
    /**
     * 是否开启RPC客户端配置类
     */
    private boolean enabled = true;
    /**
     * RPC服务器host地址列表，默认：127.0.0.1
     */
    private List<String> hosts = Arrays.asList("127.0.0.1");
    /**
     * RPC服务器端口号，默认：9999
     */
    private int port = 9999;
    /**
     * 读取超时时间，默认：10秒
     */
    private Duration readTimeOut = Duration.ofSeconds(10);
    /**
     * 连接超时时间，默认：5秒
     */
    private Duration connectTimeOut = Duration.ofSeconds(5);
    /**
     * 超过多长时间未发生读写就发送一次心跳包，默认：30秒
     */
    private Duration idleTimeOut = Duration.ofSeconds(30);
    /**
     * 连接池
     */
    private Pool pool = new Pool();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

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

    public Duration getIdleTimeOut() {
        return idleTimeOut;
    }

    public void setIdleTimeOut(Duration idleTimeOut) {
        this.idleTimeOut = idleTimeOut;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public static class Pool {
        /**
         * 最大空闲数
         */
        private int maxIdle = 5;
        /**
         * 最大链接数
         */
        private int maxTotal = 20;
        /**
         * 最小空闲数
         */
        private int minIdle = 2;

        /**
         * 初始化连接数
         */
        private int initialSize = 3;

        public int getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMaxTotal() {
            return maxTotal;
        }

        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getInitialSize() {
            return initialSize;
        }

        public void setInitialSize(int initialSize) {
            this.initialSize = initialSize;
        }
    }
}
