package com.emily.infrastructure.redis.connection;

import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.RedisProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.data.redis.connection.*;
import org.springframework.util.ClassUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author :  Emily
 * @since :  2023/9/23 21:36 PM
 */
public class RedisDbConnectionConfiguration {
    private static final boolean COMMONS_POOL2_AVAILABLE = ClassUtils.isPresent("org.apache.commons.pool2.ObjectPool", RedisDbConnectionConfiguration.class.getClassLoader());
    private final RedisDbProperties redisDbProperties;
    private final RedisStandaloneConfiguration standaloneConfiguration;
    private final RedisSentinelConfiguration sentinelConfiguration;
    private final RedisClusterConfiguration clusterConfiguration;
    private final SslBundles sslBundles;

    protected RedisDbConnectionConfiguration(RedisDbProperties redisDbProperties,
                                             ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider,
                                             ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                             ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider,
                                             ObjectProvider<SslBundles> sslBundles) {
        this.redisDbProperties = redisDbProperties;
        this.standaloneConfiguration = (RedisStandaloneConfiguration) standaloneConfigurationProvider.getIfAvailable();
        this.sentinelConfiguration = (RedisSentinelConfiguration) sentinelConfigurationProvider.getIfAvailable();
        this.clusterConfiguration = (RedisClusterConfiguration) clusterConfigurationProvider.getIfAvailable();
        this.sslBundles = (SslBundles) sslBundles.getIfAvailable();
    }

    protected final RedisStandaloneConfiguration getStandaloneConfig(RedisConnectionDetails connectionDetails) {
        if (this.standaloneConfiguration != null) {
            return this.standaloneConfiguration;
        } else {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(connectionDetails.getStandalone().getHost());
            config.setPort(connectionDetails.getStandalone().getPort());
            config.setUsername(connectionDetails.getUsername());
            config.setPassword(RedisPassword.of(connectionDetails.getPassword()));
            config.setDatabase(connectionDetails.getStandalone().getDatabase());
            return config;
        }
    }

    protected final RedisSentinelConfiguration getSentinelConfig(RedisConnectionDetails connectionDetails) {
        if (this.sentinelConfiguration != null) {
            return this.sentinelConfiguration;
        } else if (connectionDetails.getSentinel() != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(connectionDetails.getSentinel().getMaster());
            config.setSentinels(this.createSentinels(connectionDetails.getSentinel()));
            config.setUsername(connectionDetails.getUsername());
            String password = connectionDetails.getPassword();
            if (password != null) {
                config.setPassword(RedisPassword.of(password));
            }

            config.setSentinelUsername(connectionDetails.getSentinel().getUsername());
            String sentinelPassword = connectionDetails.getSentinel().getPassword();
            if (sentinelPassword != null) {
                config.setSentinelPassword(RedisPassword.of(sentinelPassword));
            }

            config.setDatabase(connectionDetails.getSentinel().getDatabase());
            return config;
        } else {
            return null;
        }
    }

    protected final RedisClusterConfiguration getClusterConfiguration(RedisProperties properties, RedisConnectionDetails connectionDetails) {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        } else {
            RedisProperties.Cluster clusterProperties = properties.getCluster();
            if (connectionDetails.getCluster() != null) {
                RedisClusterConfiguration config = new RedisClusterConfiguration(this.getNodes(connectionDetails.getCluster()));
                if (clusterProperties != null && clusterProperties.getMaxRedirects() != null) {
                    config.setMaxRedirects(clusterProperties.getMaxRedirects());
                }

                config.setUsername(connectionDetails.getUsername());
                String password = connectionDetails.getPassword();
                if (password != null) {
                    config.setPassword(RedisPassword.of(password));
                }

                return config;
            } else {
                return null;
            }
        }
    }

    private List<String> getNodes(RedisConnectionDetails.Cluster cluster) {
        return cluster.getNodes().stream().map((node) -> {
            return "%s:%d".formatted(node.host(), node.port());
        }).toList();
    }

    protected final RedisDbProperties getProperties() {
        return this.redisDbProperties;
    }

    protected SslBundles getSslBundles() {
        return this.sslBundles;
    }

    protected boolean isSslEnabled(RedisProperties properties) {
        return properties.getSsl().isEnabled();
    }

    protected boolean isPoolEnabled(RedisProperties.Pool pool) {
        Boolean enabled = pool.getEnabled();
        return enabled != null ? enabled : COMMONS_POOL2_AVAILABLE;
    }

    private List<RedisNode> createSentinels(RedisConnectionDetails.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList();
        Iterator var3 = sentinel.getNodes().iterator();

        while (var3.hasNext()) {
            RedisConnectionDetails.Node node = (RedisConnectionDetails.Node) var3.next();
            nodes.add(new RedisNode(node.host(), node.port()));
        }

        return nodes;
    }

    protected final boolean urlUsesSsl(RedisProperties properties) {
        return parseUrl(properties.getUrl()).isUseSsl();
    }

    static ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisDbUrlSyntaxException(url);
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
        } catch (URISyntaxException var8) {
            throw new RedisDbUrlSyntaxException(url, var8);
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

        URI getUri() {
            return this.uri;
        }

        boolean isUseSsl() {
            return this.useSsl;
        }

        String getUsername() {
            return this.username;
        }

        String getPassword() {
            return this.password;
        }
    }
}
