package com.emily.infrastructure.redis.connection;


import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.RedisProperties;
import com.emily.infrastructure.redis.factory.BeanFactoryProvider;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.data.redis.ClientResourcesBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientOptionsBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
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
public class LettuceDbConnectionConfiguration extends RedisDbConnectionConfiguration {

    LettuceDbConnectionConfiguration(RedisDbProperties properties,
                                     ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider,
                                     ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                     ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
                                     ObjectProvider<SslBundles> sslBundles) {
        super(properties, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider, sslBundles);
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
                                                    ClientResources clientResources,
                                                    RedisConnectionDetails connectionDetails) {
        return createConnectionFactory(builderCustomizers, clientOptionsBuilderCustomizers, clientResources, connectionDetails);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    @ConditionalOnThreading(Threading.VIRTUAL)
    LettuceConnectionFactory redisConnectionFactoryVirtualThreads(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
            ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
            ClientResources clientResources,
            RedisConnectionDetails connectionDetails) {
        LettuceConnectionFactory factory = createConnectionFactory(builderCustomizers, clientOptionsBuilderCustomizers, clientResources, connectionDetails);
        for (Map.Entry<String, RedisProperties> entry : this.getProperties().getConfig().entrySet()) {
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

    private LettuceConnectionFactory createConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
                                                             ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
                                                             ClientResources clientResources,
                                                             RedisConnectionDetails connectionDetails) {
        String defaultConfig = Objects.requireNonNull(this.getProperties().getDefaultConfig(), "Redis默认标识不可为空");
        LettuceConnectionFactory redisConnectionFactory = null;
        for (Map.Entry<String, RedisProperties> entry : this.getProperties().getConfig().entrySet()) {
            String key = entry.getKey();
            RedisProperties properties = entry.getValue();
            LettuceClientConfiguration clientConfig = this.getLettuceClientConfiguration(builderCustomizers, clientOptionsBuilderCustomizers, clientResources, properties);
            LettuceConnectionFactory connectionFactory;
            if (defaultConfig.equals(key)) {
                connectionFactory = this.createLettuceConnectionFactory(clientConfig, properties, connectionDetails);
                //是否提前初始化连接，默认：false
                connectionFactory.setEagerInitialization(properties.getLettuce().isEagerInitialization());
                //是否开启共享本地物理连接，默认：true
                connectionFactory.setShareNativeConnection(properties.getLettuce().isShareNativeConnection());
                //是否开启连接校验，默认：false
                connectionFactory.setValidateConnection(properties.getLettuce().isValidateConnection());
                //默认工厂
                redisConnectionFactory = connectionFactory;
            } else {
                RedisConnectionDetails redisConnectionDetails = BeanFactoryProvider.getBean(join(key, REDIS_CONNECT_DETAILS), RedisConnectionDetails.class);
                connectionFactory = this.createLettuceConnectionFactory(clientConfig, properties, redisConnectionDetails);
                //是否提前初始化连接，默认：false
                connectionFactory.setEagerInitialization(properties.getLettuce().isEagerInitialization());
                //是否开启共享本地物理连接，默认：true
                connectionFactory.setShareNativeConnection(properties.getLettuce().isShareNativeConnection());
                //是否开启连接校验，默认：false
                connectionFactory.setValidateConnection(properties.getLettuce().isValidateConnection());
                connectionFactory.afterPropertiesSet();
            }
            BeanFactoryProvider.registerSingleton(join(key, REDIS_CONNECTION_FACTORY), connectionFactory);
        }
        return redisConnectionFactory;
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration, RedisProperties properties, RedisConnectionDetails connectionDetails) {
        if (getSentinelConfig(connectionDetails) != null) {
            return new LettuceConnectionFactory(getSentinelConfig(connectionDetails), clientConfiguration);
        }
        if (getClusterConfiguration(properties, connectionDetails) != null) {
            return new LettuceConnectionFactory(getClusterConfiguration(properties, connectionDetails), clientConfiguration);
        }
        return new LettuceConnectionFactory(getStandaloneConfig(connectionDetails), clientConfiguration);
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
                                                                     ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientOptionsBuilderCustomizers,
                                                                     ClientResources clientResources,
                                                                     RedisProperties properties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = this.createBuilder(properties.getLettuce().getPool());
        applyProperties(builder, properties);
        if (StringUtils.hasText(properties.getUrl())) {
            customizeConfigurationFromUrl(builder, properties);
        }
        builder.clientOptions(createClientOptions(clientOptionsBuilderCustomizers, properties));
        builder.clientResources(clientResources);
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        return this.isPoolEnabled(pool) ? (new PoolBuilderFactory()).createBuilder(pool) : LettuceClientConfiguration.builder();
    }

    private void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, RedisProperties properties) {
        if (this.isSslEnabled(properties)) {
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
        }
        if (StringUtils.hasText(properties.getClientName())) {
            builder.clientName(properties.getClientName());
        }
    }

    private ClientOptions createClientOptions(ObjectProvider<LettuceClientOptionsBuilderCustomizer> clientConfigurationBuilderCustomizers, RedisProperties properties) {
        ClientOptions.Builder builder = this.initializeClientOptionsBuilder(properties);
        Duration connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }
        if (this.isSslEnabled(properties) && properties.getSsl().getBundle() != null) {
            SslBundle sslBundle = this.getSslBundles().getBundle(properties.getSsl().getBundle());
            io.lettuce.core.SslOptions.Builder sslOptionsBuilder = io.lettuce.core.SslOptions.builder();
            sslOptionsBuilder.keyManager(sslBundle.getManagers().getKeyManagerFactory());
            sslOptionsBuilder.trustManager(sslBundle.getManagers().getTrustManagerFactory());
            SslOptions sslOptions = sslBundle.getOptions();
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

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, RedisProperties properties) {
        if (this.urlUsesSsl(properties)) {
            builder.useSsl();
        }

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
            config.setMinEvictableIdleTime(properties.getMinEvictableIdleDuration());
            return config;
        }
    }
}
