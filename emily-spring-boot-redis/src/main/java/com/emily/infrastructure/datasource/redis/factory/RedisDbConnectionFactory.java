package com.emily.infrastructure.datasource.redis.factory;

import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.datasource.redis.entity.ConnectionInfo;
import com.emily.infrastructure.datasource.redis.thread.RedisDbRunnable;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: Redis连接工厂类
 * @author: Emily
 * @create: 2021/07/11
 */
public class RedisDbConnectionFactory {

    private RedisProperties properties;
    private ClientResources clientResources;

    public RedisDbConnectionFactory(RedisProperties properties, ClientResources clientResources) {
        this.properties = properties;
        this.clientResources = clientResources;
    }

    /**
     * 创建连接工厂类
     *
     * @param redisConfiguration 连接配置
     * @return
     */
    public RedisConnectionFactory createLettuceConnectionFactory(RedisConfiguration redisConfiguration) {
        //redis客户端配置
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = createBuilder(properties.getLettuce().getPool());
        if (properties.isSsl()) {
            builder.useSsl();
        }
        // Redis客户端读取超时时间
        if (Objects.nonNull(properties.getTimeout())) {
            builder.commandTimeout(properties.getTimeout());
        }
        // 关闭连接池超时时间
        if (properties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = properties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());
            }
        }
        if (StringUtils.hasText(properties.getClientName())) {
            builder.clientName(properties.getClientName());
        }
        if (StringUtils.hasText(properties.getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        // 配置ClientOptions--用于控制客户端行为的客户端选项
        builder.clientOptions(createClientOptions());
        // 配置ClientResources
        builder.clientResources(clientResources);
        // 创建基础的连接工厂类
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration, builder.build());
        // 创建Redis连接
        factory.afterPropertiesSet();
        // 将RedisConnectionFactory丢入线程池做监控
        ThreadPoolHelper.threadPoolTaskExecutor().execute(new RedisDbRunnable(factory));
        return factory;
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return new RedisPoolBuilderFactory().createBuilder(pool);
    }


    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = ConnectionInfo.parseUrl(properties.getUrl());
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
        ClientOptions.Builder builder = initializeClientOptionsBuilder();
        Duration connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }
        return builder.timeoutOptions(TimeoutOptions.enabled()).build();
    }

    private ClientOptions.Builder initializeClientOptionsBuilder() {
        if (properties.getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties = properties.getLettuce().getCluster().getRefresh();
            io.lettuce.core.cluster.ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder().dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
            if (refreshProperties.getPeriod() != null) {
                refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
            }

            if (refreshProperties.isAdaptive()) {
                refreshBuilder.enableAllAdaptiveRefreshTriggers();
            }

            return builder.topologyRefreshOptions(refreshBuilder.build());
        } else {
            return ClientOptions.builder();
        }
    }
}
