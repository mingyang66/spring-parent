package com.emily.infrastructure.redis.connection;


import com.emily.infrastructure.redis.DataRedisDbProperties;
import com.emily.infrastructure.redis.DataRedisProperties;
import com.emily.infrastructure.redis.factory.BeanFactoryProvider;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import jakarta.annotation.Nullable;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
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
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import static com.emily.infrastructure.redis.common.RedisBeanNames.*;

/**
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
public class LettuceDbConnectionConfiguration extends DataRedisDbConnectionConfiguration {


    LettuceDbConnectionConfiguration(DataRedisDbProperties properties, ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider, ObjectProvider<RedisStaticMasterReplicaConfiguration> masterReplicaConfiguration, DataRedisConnectionDetails connectionDetails) {
        super(properties, connectionDetails, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider, masterReplicaConfiguration);
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
        customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    @ConditionalOnThreading(Threading.PLATFORM)
    LettuceConnectionFactory redisConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
                                                    ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
                                                    ClientResources clientResources) {
        return createConnectionFactory(builderCustomizers, clientOptionsBuilderCustomizers, clientResources);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    @ConditionalOnThreading(Threading.VIRTUAL)
    LettuceConnectionFactory redisConnectionFactoryVirtualThreads(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
            ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
            ClientResources clientResources) {
        LettuceConnectionFactory factory = createConnectionFactory(builderCustomizers, clientOptionsBuilderCustomizers, clientResources);
        for (Map.Entry<String, DataRedisProperties> entry : this.getProperties().getConfig().entrySet()) {
            String key = entry.getKey();
            if (this.getProperties().getDefaultConfig().equals(key)) {
                factory.setExecutor(createTaskExecutor());
            } else {
                LettuceConnectionFactory connectionFactory = BeanFactoryProvider.getBean(join(key, REDIS_CONNECTION_FACTORY), LettuceConnectionFactory.class);
                connectionFactory.setExecutor(createTaskExecutor());
            }
        }
        return factory;
    }

    private SimpleAsyncTaskExecutor createTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("redis-");
        executor.setVirtualThreads(true);
        return executor;
    }

    private LettuceConnectionFactory createConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> clientConfigurationBuilderCustomizers,
                                                             ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
                                                             ClientResources clientResources) {
        String defaultConfig = Objects.requireNonNull(this.getProperties().getDefaultConfig(), "Redis默认标识不可为空");
        for (Map.Entry<String, DataRedisProperties> entry : this.getProperties().getConfig().entrySet()) {
            String key = entry.getKey();
            DataRedisProperties properties = entry.getValue();
            LettuceConnectionFactory connectionFactory;
            LettuceClientConfiguration clientConfiguration = this.getLettuceClientConfiguration(clientConfigurationBuilderCustomizers, clientOptionsBuilderCustomizers, clientResources, properties);
            DataRedisConnectionDetails dataRedisConnectionDetails = BeanFactoryProvider.getBean(join(key, REDIS_CONNECT_DETAILS), DataRedisConnectionDetails.class);
            switch (this.mode) {
                case STANDALONE:
                    connectionFactory = new LettuceConnectionFactory(this.getStandaloneConfig(dataRedisConnectionDetails), clientConfiguration);
                    break;
                case CLUSTER:
                    RedisClusterConfiguration clusterConfiguration = this.getClusterConfiguration(properties, dataRedisConnectionDetails);
                    Assert.state(clusterConfiguration != null, "'clusterConfiguration' must not be null");
                    connectionFactory = new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
                    break;
                case SENTINEL:
                    RedisSentinelConfiguration sentinelConfig = this.getSentinelConfig(dataRedisConnectionDetails);
                    Assert.state(sentinelConfig != null, "'sentinelConfig' must not be null");
                    connectionFactory = new LettuceConnectionFactory(sentinelConfig, clientConfiguration);
                    break;
                case MASTER_REPLICA:
                    RedisStaticMasterReplicaConfiguration masterReplicaConfiguration = this.getMasterReplicaConfiguration(dataRedisConnectionDetails);
                    Assert.state(masterReplicaConfiguration != null, "'masterReplicaConfig' must not be null");
                    connectionFactory = new LettuceConnectionFactory(masterReplicaConfiguration, clientConfiguration);
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }
            //是否提前初始化连接，默认：false
            connectionFactory.setEagerInitialization(properties.getLettuce().isEagerInitialization());
            //是否开启共享本地物理连接，默认：true
            connectionFactory.setShareNativeConnection(properties.getLettuce().isShareNativeConnection());
            //是否开启连接校验，默认：false
            connectionFactory.setValidateConnection(properties.getLettuce().isValidateConnection());
            if (!defaultConfig.equals(key)) {
                connectionFactory.afterPropertiesSet();
            }
            BeanFactoryProvider.registerSingleton(join(key, REDIS_CONNECTION_FACTORY), connectionFactory);
        }
        return BeanFactoryProvider.getBean(join(defaultConfig, REDIS_CONNECTION_FACTORY), LettuceConnectionFactory.class);
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> clientConfigurationBuilderCustomizers,
                                                                     ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
                                                                     ClientResources clientResources,
                                                                     DataRedisProperties properties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = this.createBuilder(properties.getLettuce().getPool());
        SslBundle sslBundle = this.getSslBundle();
        this.applyProperties(builder, sslBundle, properties);
        String url = properties.getUrl();
        if (StringUtils.hasText(url)) {
            this.customizeConfigurationFromUrl(builder, properties);
        }

        builder.clientOptions(this.createClientOptions(clientOptionsBuilderCustomizers, sslBundle, properties));
        builder.clientResources(clientResources);
        clientConfigurationBuilderCustomizers.orderedStream().forEach((customizer) -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(DataRedisProperties.Pool pool) {
        return this.isPoolEnabled(pool) ? (new PoolBuilderFactory()).createBuilder(pool) : LettuceClientConfiguration.builder();
    }


    private void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, @Nullable SslBundle sslBundle, DataRedisProperties properties) {
        if (sslBundle != null) {
            builder.useSsl();
        }

        if (properties.getTimeout() != null) {
            builder.commandTimeout(properties.getTimeout());
        }

        if (properties.getLettuce() != null) {
            DataRedisProperties.Lettuce lettuce = properties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());
            }

            String readFrom = lettuce.getReadFrom();
            if (readFrom != null) {
                builder.readFrom(this.getReadFrom(readFrom));
            }
        }

        if (StringUtils.hasText(properties.getClientName())) {
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

    private ClientOptions createClientOptions(ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientConfigurationBuilderCustomizers, @Nullable SslBundle sslBundle, DataRedisProperties properties) {
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
        return builder.build();
    }

    private ClientOptions.Builder initializeClientOptionsBuilder(DataRedisProperties properties) {
        if (properties.getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            DataRedisProperties.Lettuce.Cluster.Refresh refreshProperties = properties.getLettuce().getCluster().getRefresh();
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

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, DataRedisProperties properties) {
        if (this.urlUsesSsl(properties.getUrl())) {
            builder.useSsl();
        }

    }

    protected final boolean urlUsesSsl(String url) {
        return DataDbRedisUrl.of(url).useSsl();
    }

    private static final class PoolBuilderFactory {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(DataRedisProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(this.getPoolConfig(properties));
        }

        private GenericObjectPoolConfig<StatefulConnection<?, ?>> getPoolConfig(DataRedisProperties.Pool properties) {
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
            config.setMinEvictableIdleTime(properties.getMinEvictableIdleDuration());
            return config;
        }
    }
}
