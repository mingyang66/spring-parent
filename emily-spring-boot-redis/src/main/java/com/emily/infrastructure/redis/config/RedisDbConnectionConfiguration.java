package com.emily.infrastructure.redis.config;

import com.emily.infrastructure.redis.exception.RedisUrlSyntaxException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.*;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @program: spring-parent
 * @description: RedisConfiguration工厂配置类  RedisConnectionConfiguration
 * @author: Emily
 * @create: 2021/07/11
 */
public class RedisDbConnectionConfiguration {
    private static final boolean COMMONS_POOL2_AVAILABLE = ClassUtils.isPresent("org.apache.commons.pool2.ObjectPool", RedisDbLettuceConnectionConfiguration.class.getClassLoader());
    private RedisProperties properties;
    private final RedisStandaloneConfiguration standaloneConfiguration;
    private final RedisSentinelConfiguration sentinelConfiguration;
    private final RedisClusterConfiguration clusterConfiguration;

    public RedisDbConnectionConfiguration(RedisProperties properties, ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        this.properties = properties;
        this.standaloneConfiguration = standaloneConfigurationProvider.getIfAvailable();
        this.sentinelConfiguration = sentinelConfigurationProvider.getIfAvailable();
        this.clusterConfiguration = clusterConfigurationProvider.getIfAvailable();
    }

    protected final RedisStandaloneConfiguration getStandaloneConfig() {
        if (this.standaloneConfiguration != null) {
            return this.standaloneConfiguration;
        } else {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            if (StringUtils.hasText(this.properties.getUrl())) {
                ConnectionInfo connectionInfo = this.parseUrl(this.properties.getUrl());
                config.setHostName(connectionInfo.getHostName());
                config.setPort(connectionInfo.getPort());
                config.setUsername(connectionInfo.getUsername());
                config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
            } else {
                config.setHostName(this.properties.getHost());
                config.setPort(this.properties.getPort());
                config.setUsername(this.properties.getUsername());
                config.setPassword(RedisPassword.of(this.properties.getPassword()));
            }

            config.setDatabase(this.properties.getDatabase());
            return config;
        }
    }

    protected final RedisSentinelConfiguration getSentinelConfig() {
        if (this.sentinelConfiguration != null) {
            return this.sentinelConfiguration;
        } else {
            RedisProperties.Sentinel sentinelProperties = this.properties.getSentinel();
            if (sentinelProperties != null) {
                RedisSentinelConfiguration config = new RedisSentinelConfiguration();
                config.master(sentinelProperties.getMaster());
                config.setSentinels(this.createSentinels(sentinelProperties));
                config.setUsername(this.properties.getUsername());
                if (this.properties.getPassword() != null) {
                    config.setPassword(RedisPassword.of(this.properties.getPassword()));
                }

                if (sentinelProperties.getPassword() != null) {
                    config.setSentinelPassword(RedisPassword.of(sentinelProperties.getPassword()));
                }

                config.setDatabase(this.properties.getDatabase());
                return config;
            } else {
                return null;
            }
        }
    }

    protected final RedisClusterConfiguration getClusterConfiguration() {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        } else if (this.properties.getCluster() == null) {
            return null;
        } else {
            RedisProperties.Cluster clusterProperties = this.properties.getCluster();
            RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
            if (clusterProperties.getMaxRedirects() != null) {
                config.setMaxRedirects(clusterProperties.getMaxRedirects());
            }

            config.setUsername(this.properties.getUsername());
            if (this.properties.getPassword() != null) {
                config.setPassword(RedisPassword.of(this.properties.getPassword()));
            }

            return config;
        }
    }

    public RedisProperties getProperties() {
        return properties;
    }

    protected boolean isPoolEnabled(RedisProperties.Pool pool) {
        Boolean enabled = pool.getEnabled();
        return enabled != null ? enabled : COMMONS_POOL2_AVAILABLE;
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList();
        Iterator var3 = sentinel.getNodes().iterator();

        while (var3.hasNext()) {
            String node = (String) var3.next();

            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            } catch (RuntimeException var6) {
                throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", var6);
            }
        }

        return nodes;
    }

    protected ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisUrlSyntaxException(url);
            } else {
                boolean useSsl = "rediss".equals(scheme);
                String username = null;
                String password = null;
                if (uri.getUserInfo() != null) {
                    String candidate = uri.getUserInfo();
                    int index = candidate.indexOf(58);
                    if (index >= 0) {
                        username = candidate.substring(0, index);
                        password = candidate.substring(index + 1);
                    } else {
                        password = candidate;
                    }
                }

                return new ConnectionInfo(uri, useSsl, username, password);
            }
        } catch (URISyntaxException var9) {
            throw new RedisUrlSyntaxException(url, var9);
        }
    }

    static class ConnectionInfo {
        private final URI uri;
        private final boolean useSsl;
        private final String username;
        private final String password;

        ConnectionInfo(URI uri, boolean useSsl, String username, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.username = username;
            this.password = password;
        }

        boolean isUseSsl() {
            return this.useSsl;
        }

        String getHostName() {
            return this.uri.getHost();
        }

        int getPort() {
            return this.uri.getPort();
        }

        String getUsername() {
            return this.username;
        }

        String getPassword() {
            return this.password;
        }
    }
}
