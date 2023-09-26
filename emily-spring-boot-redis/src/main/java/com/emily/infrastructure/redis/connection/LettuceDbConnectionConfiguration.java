package com.emily.infrastructure.redis.connection;


import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.common.RedisInfo;
import io.lettuce.core.*;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.ClientResourcesBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    LettuceDbConnectionConfiguration(DefaultListableBeanFactory defaultListableBeanFactory, RedisDbProperties properties, ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider, ObjectProvider<SslBundles> sslBundles) {
        super(properties, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider, sslBundles);
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean(
            destroyMethod = "shutdown"
    )
    @ConditionalOnMissingBean({ClientResources.class})
    DefaultClientResources lettuceClientResources(ObjectProvider<ClientResourcesBuilderCustomizer> customizers) {
        DefaultClientResources.Builder builder = DefaultClientResources.builder();
        customizers.orderedStream().forEach((customizer) -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    LettuceConnectionFactory redisConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources, RedisConnectionDetails connectionDetails) {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(this.getProperties().getConfig(), "Redis连接配置不存在");
        LettuceConnectionFactory redisConnectionFactory = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RedisProperties properties = entry.getValue();
            LettuceClientConfiguration clientConfig = this.getLettuceClientConfiguration(builderCustomizers, clientResources, properties.getLettuce().getPool(), key);
            if (this.getProperties().getDefaultConfig().equals(key)) {
                this.setConnectionDetails(connectionDetails);
                redisConnectionFactory = this.createLettuceConnectionFactory(clientConfig, key);
            } else {
                this.setConnectionDetails(defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECT_DETAILS, RedisConnectionDetails.class));
                LettuceConnectionFactory connectionFactory = this.createLettuceConnectionFactory(clientConfig, key);
                connectionFactory.afterPropertiesSet();
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.REDIS_CONNECTION_FACTORY, connectionFactory);
            }
        }
        return redisConnectionFactory;
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration, String key) {
        if (this.getSentinelConfig() != null) {
            return new LettuceConnectionFactory(this.getSentinelConfig(), clientConfiguration);
        } else {
            return this.getClusterConfiguration(key) != null ? new LettuceConnectionFactory(this.getClusterConfiguration(key), clientConfiguration) : new LettuceConnectionFactory(this.getStandaloneConfig(), clientConfiguration);
        }
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources, RedisProperties.Pool pool, String key) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = this.createBuilder(pool);
        this.applyProperties(builder, key);
        if (StringUtils.hasText(this.getProperties().getConfig().get(key).getUrl())) {
            this.customizeConfigurationFromUrl(builder, key);
        }

        builder.clientOptions(this.createClientOptions(key));
        builder.clientResources(clientResources);
        builderCustomizers.orderedStream().forEach((customizer) -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        return this.isPoolEnabled(pool) ? (new LettuceDbConnectionConfiguration.PoolBuilderFactory()).createBuilder(pool) : LettuceClientConfiguration.builder();
    }

    private void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, String key) {
        RedisProperties properties = this.getProperties().getConfig().get(key);
        if (this.isSslEnabled(key)) {
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

    private ClientOptions createClientOptions(String key) {
        RedisProperties properties = this.getProperties().getConfig().get(key);
        ClientOptions.Builder builder = this.initializeClientOptionsBuilder(properties);
        Duration connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }

        if (this.isSslEnabled(key) && properties.getSsl().getBundle() != null) {
            SslBundle sslBundle = this.getSslBundles().getBundle(properties.getSsl().getBundle());
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

        return builder.timeoutOptions(TimeoutOptions.enabled()).build();
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

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, String key) {
        if (this.urlUsesSsl(key)) {
            builder.useSsl();
        }

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
