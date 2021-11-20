package com.emily.infrastructure.redis.config;

import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.redis.example.RedisDbRunnable;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * @program: spring-parent
 * @description: Redis连接工厂类
 * @author: Emily
 * @create: 2021/07/11
 */
public class RedisDbLettuceConnectionConfiguration extends RedisDbConnectionConfiguration {
    /**
     * 是否开启共享本地连接校验，默认：false
     * 如果校验失败，则新建连接
     */
    private boolean validateConnection = false;
    /**
     * 是否开启共享本地物理连接，默认：true
     */
    private boolean shareNativeConnection = true;

    public RedisDbLettuceConnectionConfiguration(RedisProperties properties, ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        super(properties, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider);
    }

    /**
     * 创建连接工厂类
     *
     * @return
     */
    public LettuceConnectionFactory getRedisConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {

        Assert.notNull(clientResources, "ClientResources must not be null");
        Assert.notNull(clientResources, "RedisDbProperties must not be null");

        LettuceClientConfiguration clientConfig = this.getLettuceClientConfiguration(builderCustomizers, clientResources, this.getProperties().getLettuce().getPool());
        return this.createLettuceConnectionFactory(clientConfig);
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration) {
        LettuceConnectionFactory factory;
        if (this.getSentinelConfig() != null) {
            factory = new LettuceConnectionFactory(this.getSentinelConfig(), clientConfiguration);
        } else {
            factory = this.getClusterConfiguration() != null ? new LettuceConnectionFactory(this.getClusterConfiguration(), clientConfiguration) : new LettuceConnectionFactory(this.getStandaloneConfig(), clientConfiguration);
        }
        return initLettuceConnectionFactory(factory);
    }

    private LettuceConnectionFactory initLettuceConnectionFactory(LettuceConnectionFactory factory) {
        //开启LettuceConnection本地共享连接校验，默认：false
        //如果校验失败，则会将之前连接关闭，创建新的连接
        factory.setValidateConnection(this.isValidateConnection());
        //开启多个LettuceConnection链接时共享单个本机链接，默认为：true;
        //如果设置为false,则每一个LettuceConnection链接操作都要打开和关闭一个socket
        factory.setShareNativeConnection(this.isShareNativeConnection());
        //是否系统启动时提前初始化共享本地连接
        factory.setEagerInitialization(false);
        // 创建Redis连接
        factory.afterPropertiesSet();
        // 将RedisConnectionFactory丢入线程池做监控
        ThreadPoolHelper.threadPoolTaskExecutor().execute(new RedisDbRunnable(factory));
        return factory;
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources, RedisProperties.Pool pool) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = this.createBuilder(pool);
        this.applyProperties(builder);
        if (StringUtils.hasText(this.getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builder.clientOptions(this.createClientOptions());
        builder.clientResources(clientResources);
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        return this.isPoolEnabled(pool) ? (new PoolBuilderFactory()).createBuilder(pool) : LettuceClientConfiguration.builder();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder applyProperties(
            LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (this.getProperties().isSsl()) {
            builder.useSsl();
        }
        // Redis客户端读取超时时间
        if (this.getProperties().getTimeout() != null) {
            builder.commandTimeout(this.getProperties().getTimeout());
        }
        // 关闭连接池超时时间
        if (this.getProperties().getLettuce() != null) {
            RedisProperties.Lettuce lettuce = this.getProperties().getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(this.getProperties().getLettuce().getShutdownTimeout());
            }
        }
        if (StringUtils.hasText(this.getProperties().getClientName())) {
            builder.clientName(this.getProperties().getClientName());
        }
        return builder;
    }

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = this.parseUrl(this.getProperties().getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    /**
     * ClientOptions 用于控制客户端行为的客户端选项
     *
     * @return
     */
    private ClientOptions createClientOptions() {
        ClientOptions.Builder builder = this.initializeClientOptionsBuilder();
        Duration connectTimeout = this.getProperties().getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }
        return builder.timeoutOptions(TimeoutOptions.enabled()).build();
    }

    /**
     * 拓扑刷新
     * 开启 自适应集群拓扑刷新和周期拓扑刷新
     * https://github.com/lettuce-io/lettuce-core/wiki/Redis-Cluster#user-content-refreshing-the-cluster-topology-view
     *
     * @return
     */
    private ClientOptions.Builder initializeClientOptionsBuilder() {
        if (this.getProperties().getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties = this.getProperties().getLettuce().getCluster().getRefresh();
            ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder()
                    .dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
            if (refreshProperties.getPeriod() != null) {
                /**
                 * 开启周期刷新
                 */
                refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
            }

            if (refreshProperties.isAdaptive()) {
                /**
                 * 开启自适应刷新,自适应刷新不开启,Redis集群变更时将会导致连接异常
                 */
                refreshBuilder.enableAllAdaptiveRefreshTriggers();
            }

            return builder.topologyRefreshOptions(refreshBuilder.build());
        } else {
            return ClientOptions.builder();
        }
    }


    public boolean isValidateConnection() {
        return validateConnection;
    }

    public void setValidateConnection(boolean validateConnection) {
        this.validateConnection = validateConnection;
    }

    public boolean isShareNativeConnection() {
        return shareNativeConnection;
    }

    public void setShareNativeConnection(boolean shareNativeConnection) {
        this.shareNativeConnection = shareNativeConnection;
    }


    private static class PoolBuilderFactory {
        private PoolBuilderFactory() {
        }

        LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(this.getPoolConfig(properties));
        }

        private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRuns(properties.getTimeBetweenEvictionRuns());
            }

            if (properties.getMaxWait() != null) {
                config.setMaxWait(properties.getMaxWait());
            }

            return config;
        }
    }
}
