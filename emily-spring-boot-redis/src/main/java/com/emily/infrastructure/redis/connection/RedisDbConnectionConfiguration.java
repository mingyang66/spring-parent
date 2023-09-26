package com.emily.infrastructure.redis.connection;

import com.emily.infrastructure.redis.RedisDbProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
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
    private RedisConnectionDetails connectionDetails;
    private final SslBundles sslBundles;

    protected RedisDbConnectionConfiguration(RedisDbProperties redisDbProperties, ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider, ObjectProvider<SslBundles> sslBundles) {
        this.redisDbProperties = redisDbProperties;
        this.standaloneConfiguration = (RedisStandaloneConfiguration) standaloneConfigurationProvider.getIfAvailable();
        this.sentinelConfiguration = (RedisSentinelConfiguration) sentinelConfigurationProvider.getIfAvailable();
        this.clusterConfiguration = (RedisClusterConfiguration) clusterConfigurationProvider.getIfAvailable();
        this.sslBundles = (SslBundles) sslBundles.getIfAvailable();
    }

    protected final RedisStandaloneConfiguration getStandaloneConfig() {
        if (this.standaloneConfiguration != null) {
            return this.standaloneConfiguration;
        } else {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(this.connectionDetails.getStandalone().getHost());
            config.setPort(this.connectionDetails.getStandalone().getPort());
            config.setUsername(this.connectionDetails.getUsername());
            config.setPassword(RedisPassword.of(this.connectionDetails.getPassword()));
            config.setDatabase(this.connectionDetails.getStandalone().getDatabase());
            return config;
        }
    }

    protected final RedisSentinelConfiguration getSentinelConfig() {
        if (this.sentinelConfiguration != null) {
            return this.sentinelConfiguration;
        } else if (this.connectionDetails.getSentinel() != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(this.connectionDetails.getSentinel().getMaster());
            config.setSentinels(this.createSentinels(this.connectionDetails.getSentinel()));
            config.setUsername(this.connectionDetails.getUsername());
            String password = this.connectionDetails.getPassword();
            if (password != null) {
                config.setPassword(RedisPassword.of(password));
            }

            config.setSentinelUsername(this.connectionDetails.getSentinel().getUsername());
            String sentinelPassword = this.connectionDetails.getSentinel().getPassword();
            if (sentinelPassword != null) {
                config.setSentinelPassword(RedisPassword.of(sentinelPassword));
            }

            config.setDatabase(this.connectionDetails.getSentinel().getDatabase());
            return config;
        } else {
            return null;
        }
    }

    protected final RedisClusterConfiguration getClusterConfiguration(String key) {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        } else {
            RedisProperties.Cluster clusterProperties = this.redisDbProperties.getConfig().get(key).getCluster();
            if (this.connectionDetails.getCluster() != null) {
                RedisClusterConfiguration config = new RedisClusterConfiguration(this.getNodes(this.connectionDetails.getCluster()));
                if (clusterProperties != null && clusterProperties.getMaxRedirects() != null) {
                    config.setMaxRedirects(clusterProperties.getMaxRedirects());
                }

                config.setUsername(this.connectionDetails.getUsername());
                String password = this.connectionDetails.getPassword();
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

    protected boolean isSslEnabled(String key) {
        return this.getProperties().getConfig().get(key).getSsl().isEnabled();
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

    protected final boolean urlUsesSsl(String key) {
        return parseUrl(this.redisDbProperties.getConfig().get(key).getUrl()).isUseSsl();
    }

    protected final RedisConnectionDetails getConnectionDetails() {
        return this.connectionDetails;
    }

    public void setConnectionDetails(RedisConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
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
