package com.emily.infrastructure.rabbitmq;

import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;


/**
 * RabbitMq属性配置
 *
 * @author Emily
 * @since Created in 2022/6/2 5:06 下午
 */
@ConfigurationProperties(prefix = DataRabbitProperties.PREFIX)
public class DataRabbitProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.rabbit";
    /**
     * 是否开启RabbitMq组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 默认配置
     */
    private String defaultConfig;
    /**
     * 存储发送、退回、接收的消息到日志平台
     */
    private boolean storeLogMessages = true;

    /**
     * 连接工厂配置
     */
    private final Connection connection = new Connection();
    /**
     * RabbitMq属性配置
     */
    private final Map<String, RabbitProperties> config = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Map<String, RabbitProperties> getConfig() {
        return config;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isStoreLogMessages() {
        return storeLogMessages;
    }

    public void setStoreLogMessages(boolean storeLogMessages) {
        this.storeLogMessages = storeLogMessages;
    }
    
    public static class Connection {
        /**
         * 设置TCP连接超时时间，默认：60000ms
         */
        private int connectionTimeout = 60000;
        /**
         * 启用或禁用连接自动恢复，默认：true
         */
        private boolean automaticRecovery = true;
        /**
         * 设置连接恢复时间间隔，默认：5000ms
         */
        private long networkRecoveryInterval = 5000;
        /**
         * 启用或禁用拓扑恢复，默认：true【拓扑恢复功能可以帮助消费者重新声明之前定义的队列、交换机和绑定等拓扑结构】
         */
        private boolean topologyRecovery = true;
        /**
         * 监听器类型
         */
        private RabbitProperties.ContainerType listenerType = RabbitProperties.ContainerType.SIMPLE;

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public boolean isAutomaticRecovery() {
            return automaticRecovery;
        }

        public void setAutomaticRecovery(boolean automaticRecovery) {
            this.automaticRecovery = automaticRecovery;
        }

        public long getNetworkRecoveryInterval() {
            return networkRecoveryInterval;
        }

        public void setNetworkRecoveryInterval(long networkRecoveryInterval) {
            this.networkRecoveryInterval = networkRecoveryInterval;
        }

        public boolean isTopologyRecovery() {
            return topologyRecovery;
        }

        public void setTopologyRecovery(boolean topologyRecovery) {
            this.topologyRecovery = topologyRecovery;
        }

        public RabbitProperties.ContainerType getListenerType() {
            return listenerType;
        }

        public void setListenerType(RabbitProperties.ContainerType listenerType) {
            this.listenerType = listenerType;
        }
    }
}
