package com.emily.infrastructure.redis.connection;


import com.emily.infrastructure.redis.DataDbRedisProperties;
import com.emily.infrastructure.redis.RedisProperties;
import com.emily.infrastructure.redis.common.DataRedisInfo;
import com.emily.infrastructure.redis.tracing.LoggingTracing;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.data.redis.autoconfigure.ClientResourcesBuilderCustomizer;
import org.springframework.boot.data.redis.autoconfigure.DataRedisConnectionDetails;
import org.springframework.boot.data.redis.autoconfigure.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.data.redis.autoconfigure.LettuceClientOptionsBuilderCustomizer;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.thread.Threading;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Map;


/**
 * org.springframework.boot.data.redis.autoconfigure.LettuceConnectionConfiguration
 *
 * @author :  Emily
 * @since :  2023/9/23 21:35 PM
 */
@Configuration(
        proxyBeanMethods = false
)
@ConditionalOnClass({RedisClient.class})
@ConditionalOnProperty(
        name = {"spring.emily.redis.client-type"},
        havingValue = "lettuce",
        matchIfMissing = true
)
public class DataDbLettuceConnectionConfiguration extends DataDbRedisConnectionConfiguration {
    private final DefaultListableBeanFactory beanFactory;

    DataDbLettuceConnectionConfiguration(DataDbRedisProperties properties,
                                         ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider,
                                         ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                         ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
                                         ObjectProvider<RedisStaticMasterReplicaConfiguration> masterReplicaConfiguration,
                                         DataRedisConnectionDetails connectionDetails,
                                         DefaultListableBeanFactory beanFactory) {
        super(properties, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider, masterReplicaConfiguration, connectionDetails);
        this.beanFactory = beanFactory;
    }

    /**
     * 默认客户端资源配置，包括连接池、线程池、事件总线、DNS解析器、地址分组解析器等
     *
     * @param customizers 自定义客户端资源实现
     * @return 客户端资源配置
     * @see DefaultClientResources#shutdown() 方法是关闭和释放Redis客户端资源
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    DefaultClientResources lettuceClientResources(ObjectProvider<ClientResourcesBuilderCustomizer> customizers) {
        DefaultClientResources.Builder builder = DefaultClientResources.builder();
        if (this.getProperties().isTracing()) {
            builder.tracing(new LoggingTracing(true));
        }
        customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    @Bean(DataRedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY)
    @Primary
    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    @ConditionalOnThreading(Threading.PLATFORM)
    LettuceConnectionFactory redisConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
                                                    ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
                                                    ClientResources clientResources,
                                                    Map<String, DataRedisConnectionDetails> connectionDetails) {
        for (Map.Entry<String, RedisProperties> entry : this.getProperties().getConfig().entrySet()) {
            LettuceConnectionFactory factory = createConnectionFactory(builderCustomizers,
                    clientOptionsBuilderCustomizers,
                    clientResources,
                    connectionDetails.get(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_CONNECT_DETAILS)),
                    entry.getValue());
            //是否提前初始化连接，默认：false
            factory.setEagerInitialization(entry.getValue().getLettuce().isEagerInitialization());
            //是否开启共享本地物理连接，默认：true
            factory.setShareNativeConnection(entry.getValue().getLettuce().isShareNativeConnection());
            //是否开启连接校验，默认：false
            factory.setValidateConnection(entry.getValue().getLettuce().isValidateConnection());
            if (!this.getProperties().getDefaultConfig().equals(entry.getKey())) {
                factory.afterPropertiesSet();
            }
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_CONNECTION_FACTORY), factory);
        }
        return beanFactory.getBean(StringUtils.join(this.getProperties().getDefaultConfig(), DataRedisInfo.REDIS_CONNECTION_FACTORY), LettuceConnectionFactory.class);
    }

    @Bean(name = DataRedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY)
    @Primary
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    @ConditionalOnThreading(Threading.VIRTUAL)
    LettuceConnectionFactory redisConnectionFactoryVirtualThreads(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
            ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
            ClientResources clientResources,
            Map<String, DataRedisConnectionDetails> connectionDetails) {
        for (Map.Entry<String, RedisProperties> entry : this.getProperties().getConfig().entrySet()) {
            LettuceConnectionFactory factory = createConnectionFactory(builderCustomizers,
                    clientOptionsBuilderCustomizers,
                    clientResources,
                    connectionDetails.get(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_CONNECT_DETAILS)),
                    entry.getValue());
            //是否提前初始化连接，默认：false
            factory.setEagerInitialization(entry.getValue().getLettuce().isEagerInitialization());
            //是否开启共享本地物理连接，默认：true
            factory.setShareNativeConnection(entry.getValue().getLettuce().isShareNativeConnection());
            //是否开启连接校验，默认：false
            factory.setValidateConnection(entry.getValue().getLettuce().isValidateConnection());

            SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("redis-");
            executor.setVirtualThreads(true);
            factory.setExecutor(executor);
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_CONNECTION_FACTORY), factory);
        }
        return beanFactory.getBean(StringUtils.join(this.getProperties().getDefaultConfig(), DataRedisInfo.REDIS_CONNECTION_FACTORY), LettuceConnectionFactory.class);
    }

    private LettuceConnectionFactory createConnectionFactory(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> clientConfigurationBuilderCustomizers,
            ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
            ClientResources clientResources,
            DataRedisConnectionDetails redisConnectionDetails,
            RedisProperties properties) {
        LettuceClientConfiguration clientConfiguration = getLettuceClientConfiguration(clientConfigurationBuilderCustomizers,
                clientOptionsBuilderCustomizers,
                clientResources,
                redisConnectionDetails,
                properties);
        return switch (this.mode) {
            case STANDALONE ->
                    new LettuceConnectionFactory(getStandaloneConfig(redisConnectionDetails), clientConfiguration);
            case CLUSTER -> {
                RedisClusterConfiguration clusterConfiguration = getClusterConfiguration(properties, redisConnectionDetails);
                Assert.state(clusterConfiguration != null, "'clusterConfiguration' must not be null");
                yield new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
            }
            case SENTINEL -> {
                RedisSentinelConfiguration sentinelConfig = getSentinelConfig(redisConnectionDetails);
                Assert.state(sentinelConfig != null, "'sentinelConfig' must not be null");
                yield new LettuceConnectionFactory(sentinelConfig, clientConfiguration);
            }
            case MASTER_REPLICA -> {
                RedisStaticMasterReplicaConfiguration masterReplicaConfiguration = getMasterReplicaConfiguration(redisConnectionDetails);
                Assert.state(masterReplicaConfiguration != null, "'masterReplicaConfig' must not be null");
                yield new LettuceConnectionFactory(masterReplicaConfiguration, clientConfiguration);
            }
        };
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> clientConfigurationBuilderCustomizers,
                                                                     ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
                                                                     ClientResources clientResources,
                                                                     DataRedisConnectionDetails connectionDetails,
                                                                     RedisProperties properties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = this.createBuilder(properties.getLettuce().getPool());
        SslBundle sslBundle = this.getSslBundle(connectionDetails);
        this.applyProperties(builder, sslBundle, properties);
        String url = properties.getUrl();
        if (org.springframework.util.StringUtils.hasText(url)) {
            customizeConfigurationFromUrl(builder, properties);
        }

        builder.clientOptions(this.createClientOptions(clientOptionsBuilderCustomizers, sslBundle, properties));
        builder.clientResources(clientResources);
        clientConfigurationBuilderCustomizers.orderedStream().forEach((customizer) -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool
                                                                                               pool) {
        return this.isPoolEnabled(pool) ? (new PoolBuilderFactory()).createBuilder(pool) : LettuceClientConfiguration.builder();
    }


    private void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder
                                         builder, @Nullable SslBundle sslBundle, RedisProperties properties) {
        if (sslBundle != null) {
            builder.useSsl();
        }

        if (properties.getTimeout() != null) {
            builder.commandTimeout(properties.getTimeout());
        }

        if (properties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = properties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());
            }

            String readFrom = lettuce.getReadFrom();
            if (readFrom != null) {
                builder.readFrom(this.getReadFrom(readFrom));
            }
        }

        if (org.springframework.util.StringUtils.hasText(properties.getClientName())) {
            builder.clientName(properties.getClientName());
        }

    }

    private ReadFrom getReadFrom(String readFrom) {
        int index = readFrom.indexOf(58);
        if (index == -1) {
            return ReadFrom.valueOf(this.getCanonicalReadFromName(readFrom));
        } else {
            String name = this.getCanonicalReadFromName(readFrom.substring(0, index));
            String value = readFrom.substring(index + 1);
            return ReadFrom.valueOf(name + ":" + value);
        }
    }

    private String getCanonicalReadFromName(String name) {
        StringBuilder canonicalName = new StringBuilder(name.length());
        name.chars().filter(Character::isLetterOrDigit).map(Character::toLowerCase).forEach((c) -> {
            canonicalName.append((char) c);
        });
        return canonicalName.toString();
    }

    private ClientOptions createClientOptions(ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientConfigurationBuilderCustomizers,
                                              @Nullable SslBundle sslBundle,
                                              RedisProperties properties) {
        ClientOptions.Builder builder = this.initializeClientOptionsBuilder(properties);
        Duration connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }

        if (sslBundle != null) {
            SslOptions.Builder sslOptionsBuilder = SslOptions.builder();
            sslOptionsBuilder.keyManager(sslBundle.getManagers().getKeyManagerFactory());
            sslOptionsBuilder.trustManager(sslBundle.getManagers().getTrustManagerFactory());
            org.springframework.boot.ssl.SslOptions sslOptions = sslBundle.getOptions();
            if (sslOptions.getCiphers() != null) {
                sslOptionsBuilder.cipherSuites(sslOptions.getCiphers());
            }

            if (sslOptions.getEnabledProtocols() != null) {
                sslOptionsBuilder.protocols(sslOptions.getEnabledProtocols());
            }

            builder.sslOptions(sslOptionsBuilder.build());
        }

        builder.timeoutOptions(TimeoutOptions.enabled());
        clientConfigurationBuilderCustomizers.orderedStream().forEach((customizer) -> {
            customizer.customize(builder);
        });
        //协议版本号
        builder.protocolVersion(properties.getProtocolVersion());
        return builder.build();
    }

    private ClientOptions.Builder initializeClientOptionsBuilder(RedisProperties properties) {
        if (properties.getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties = properties.getLettuce().getCluster().getRefresh();
            ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder().dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
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

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder
                                                       builder, RedisProperties properties) {
        if (this.urlUsesSsl(properties.getUrl())) {
            builder.useSsl();
        }

    }

    protected final boolean urlUsesSsl(String url) {
        return DataDbRedisUrl.of(url).useSsl();
    }

    private static final class PoolBuilderFactory {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(this.getPoolConfig(properties));
        }

        private GenericObjectPoolConfig<StatefulConnection<?, ?>> getPoolConfig(RedisProperties.Pool properties) {
            GenericObjectPoolConfig<StatefulConnection<?, ?>> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRuns(properties.getTimeBetweenEvictionRuns());
            }

            if (properties.getMaxWait() != null) {
                config.setMaxWait(properties.getMaxWait());
            }
            //自定义连接空闲时长
            config.setMinEvictableIdleDuration(properties.getMinEvictableIdleDuration());
            return config;
        }
    }
}
