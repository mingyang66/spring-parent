package com.emily.infrastructure.redis.connection;


import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.common.RedisInfo;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.SSLParameters;
import java.util.Map;
import java.util.Objects;

/**
 * @author :  Emily
 * @since :  2023/9/23 21:50 PM
 */
@Configuration(
        proxyBeanMethods = false
)
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
@ConditionalOnMissingBean({RedisConnectionFactory.class})
@ConditionalOnProperty(
        name = {"spring.emily.redis.client-type"},
        havingValue = "jedis",
        matchIfMissing = true
)
public class JedisDbConnectionConfiguration extends RedisDbConnectionConfiguration {

    private final DefaultListableBeanFactory defaultListableBeanFactory;

    JedisDbConnectionConfiguration(DefaultListableBeanFactory defaultListableBeanFactory, RedisDbProperties properties,
                                   ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider,
                                   ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                                   ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
                                   ObjectProvider<SslBundles> sslBundles) {
        super(properties, standaloneConfigurationProvider, sentinelConfiguration,
                clusterConfiguration, sslBundles);
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean
    JedisConnectionFactory redisConnectionFactory(
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers, RedisConnectionDetails connectionDetails) {
        return createJedisConnectionFactory(builderCustomizers, connectionDetails);
    }

    private JedisConnectionFactory createJedisConnectionFactory(
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers, RedisConnectionDetails connectionDetails) {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(this.getProperties().getConfig(), "Redis连接配置不存在");
        JedisConnectionFactory redisConnectionFactory = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RedisProperties properties = entry.getValue();
            JedisClientConfiguration clientConfiguration = getJedisClientConfiguration(builderCustomizers, properties, key);
            if (this.getProperties().getDefaultConfig().equals(key)) {
                this.setConnectionDetails(connectionDetails);
            } else {
                this.setConnectionDetails(defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECT_DETAILS, RedisConnectionDetails.class));
            }
            JedisConnectionFactory jedisConnectionFactory;
            if (getSentinelConfig() != null) {
                jedisConnectionFactory = new JedisConnectionFactory(getSentinelConfig(), clientConfiguration);
            } else if (getClusterConfiguration(key) != null) {
                jedisConnectionFactory = new JedisConnectionFactory(getClusterConfiguration(key), clientConfiguration);
            } else {
                jedisConnectionFactory = new JedisConnectionFactory(getStandaloneConfig(), clientConfiguration);
            }
            if (this.getProperties().getDefaultConfig().equals(key)) {
                redisConnectionFactory = jedisConnectionFactory;
            } else {
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.REDIS_CONNECTION_FACTORY, jedisConnectionFactory);
            }
        }
        return redisConnectionFactory;
    }

    private JedisClientConfiguration getJedisClientConfiguration(
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers, RedisProperties properties, String key) {
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder(), properties);
        if (isSslEnabled(key)) {
            applySsl(builder, properties);
        }
        RedisProperties.Pool pool = properties.getJedis().getPool();
        if (isPoolEnabled(pool)) {
            applyPooling(pool, builder);
        }
        if (StringUtils.hasText(properties.getUrl())) {
            customizeConfigurationFromUrl(builder, key);
        }
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private JedisClientConfiguration.JedisClientConfigurationBuilder applyProperties(JedisClientConfiguration.JedisClientConfigurationBuilder builder, RedisProperties properties) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(properties.getTimeout()).to(builder::readTimeout);
        map.from(properties.getConnectTimeout()).to(builder::connectTimeout);
        map.from(properties.getClientName()).whenHasText().to(builder::clientName);
        return builder;
    }

    private void applySsl(JedisClientConfiguration.JedisClientConfigurationBuilder builder, RedisProperties properties) {
        JedisClientConfiguration.JedisSslClientConfigurationBuilder sslBuilder = builder.useSsl();
        if (properties.getSsl().getBundle() != null) {
            SslBundle sslBundle = getSslBundles().getBundle(properties.getSsl().getBundle());
            sslBuilder.sslSocketFactory(sslBundle.createSslContext().getSocketFactory());
            SslOptions sslOptions = sslBundle.getOptions();
            SSLParameters sslParameters = new SSLParameters();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(sslOptions.getCiphers()).to(sslParameters::setCipherSuites);
            map.from(sslOptions.getEnabledProtocols()).to(sslParameters::setProtocols);
            sslBuilder.sslParameters(sslParameters);
        }
    }

    private void applyPooling(RedisProperties.Pool pool,
                              JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }

    private JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRuns(pool.getTimeBetweenEvictionRuns());
        }
        if (pool.getMaxWait() != null) {
            config.setMaxWait(pool.getMaxWait());
        }
        return config;
    }

    private void customizeConfigurationFromUrl(JedisClientConfiguration.JedisClientConfigurationBuilder builder, String key) {
        if (urlUsesSsl(key)) {
            builder.useSsl();
        }
    }

}
