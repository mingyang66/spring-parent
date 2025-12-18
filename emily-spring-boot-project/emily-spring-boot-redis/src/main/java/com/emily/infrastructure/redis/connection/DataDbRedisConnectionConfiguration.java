package com.emily.infrastructure.redis.connection;

import com.emily.infrastructure.redis.DataDbRedisProperties;
import com.emily.infrastructure.redis.RedisProperties;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.data.redis.autoconfigure.DataRedisConnectionDetails;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.data.redis.connection.*;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * org.springframework.boot.data.redis.autoconfigure.DataRedisConnectionConfiguration
 *
 * @author :  Emily
 * @since :  2023/9/23 21:36 PM
 */
abstract class DataDbRedisConnectionConfiguration {
    private static final boolean COMMONS_POOL2_AVAILABLE = ClassUtils.isPresent("org.apache.commons.pool2.ObjectPool", DataDbRedisConnectionConfiguration.class.getClassLoader());
    private final DataDbRedisProperties properties;
    private final @Nullable RedisStandaloneConfiguration standaloneConfiguration;
    private final @Nullable RedisSentinelConfiguration sentinelConfiguration;
    private final @Nullable RedisClusterConfiguration clusterConfiguration;
    private final @Nullable RedisStaticMasterReplicaConfiguration masterReplicaConfiguration;
    private final DataRedisConnectionDetails connectionDetails;
    protected final Mode mode;

    protected DataDbRedisConnectionConfiguration(DataDbRedisProperties properties, DataRedisConnectionDetails connectionDetails, ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider, ObjectProvider<RedisStaticMasterReplicaConfiguration> masterReplicaConfiguration) {
        this.properties = properties;
        this.standaloneConfiguration = (RedisStandaloneConfiguration) standaloneConfigurationProvider.getIfAvailable();
        this.sentinelConfiguration = (RedisSentinelConfiguration) sentinelConfigurationProvider.getIfAvailable();
        this.clusterConfiguration = (RedisClusterConfiguration) clusterConfigurationProvider.getIfAvailable();
        this.masterReplicaConfiguration = (RedisStaticMasterReplicaConfiguration) masterReplicaConfiguration.getIfAvailable();
        this.connectionDetails = connectionDetails;
        this.mode = this.determineMode(properties.getDefaultDataSource(), connectionDetails);
    }

    protected final RedisStandaloneConfiguration getStandaloneConfig(DataRedisConnectionDetails connectionDetails) {
        if (this.standaloneConfiguration != null) {
            return this.standaloneConfiguration;
        } else {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            DataRedisConnectionDetails.Standalone standalone = connectionDetails.getStandalone();
            Assert.state(standalone != null, "'standalone' must not be null");
            config.setHostName(standalone.getHost());
            config.setPort(standalone.getPort());
            config.setUsername(connectionDetails.getUsername());
            config.setPassword(RedisPassword.of(connectionDetails.getPassword()));
            config.setDatabase(standalone.getDatabase());
            return config;
        }
    }


    protected final @Nullable RedisSentinelConfiguration getSentinelConfig(DataRedisConnectionDetails connectionDetails) {
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


    protected final @Nullable RedisClusterConfiguration getClusterConfiguration(RedisProperties properties, DataRedisConnectionDetails connectionDetails) {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        } else {
            RedisProperties.Cluster clusterProperties = properties.getCluster();
            if (connectionDetails.getCluster() != null) {
                RedisClusterConfiguration config = new RedisClusterConfiguration();
                config.setClusterNodes(this.getNodes(connectionDetails.getCluster()));
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

    protected final @Nullable RedisStaticMasterReplicaConfiguration getMasterReplicaConfiguration(DataRedisConnectionDetails connectionDetails) {
        if (this.masterReplicaConfiguration != null) {
            return this.masterReplicaConfiguration;
        } else if (connectionDetails.getMasterReplica() != null) {
            List<DataRedisConnectionDetails.Node> nodes = connectionDetails.getMasterReplica().getNodes();
            Assert.state(!nodes.isEmpty(), "At least one node is required for master-replica configuration");
            RedisStaticMasterReplicaConfiguration config = new RedisStaticMasterReplicaConfiguration(((DataRedisConnectionDetails.Node) nodes.get(0)).host(), ((DataRedisConnectionDetails.Node) nodes.get(0)).port());
            nodes.stream().skip(1L).forEach((node) -> {
                config.addNode(node.host(), node.port());
            });
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

    private List<RedisNode> getNodes(DataRedisConnectionDetails.Cluster cluster) {
        return cluster.getNodes().stream().map(this::asRedisNode).toList();
    }

    private RedisNode asRedisNode(DataRedisConnectionDetails.Node node) {
        return new RedisNode(node.host(), node.port());
    }

    protected @Nullable SslBundle getSslBundle() {
        return this.connectionDetails.getSslBundle();
    }

    protected boolean isSslEnabled(RedisProperties properties) {
        return properties.getSsl().isEnabled();
    }

    protected boolean isPoolEnabled(RedisProperties.Pool pool) {
        Boolean enabled = pool.getEnabled();
        return (enabled != null) ? enabled : COMMONS_POOL2_AVAILABLE;
    }

    private List<RedisNode> createSentinels(DataRedisConnectionDetails.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList();
        Iterator var3 = sentinel.getNodes().iterator();

        while (var3.hasNext()) {
            DataRedisConnectionDetails.Node node = (DataRedisConnectionDetails.Node) var3.next();
            nodes.add(this.asRedisNode(node));
        }

        return nodes;
    }

    public DataDbRedisProperties getProperties() {
        return properties;
    }

    protected final DataRedisConnectionDetails getConnectionDetails() {
        return this.connectionDetails;
    }


    private Mode determineMode(RedisProperties properties, DataRedisConnectionDetails connectionDetails) {
        if (this.getSentinelConfig(connectionDetails) != null) {
            return Mode.SENTINEL;
        } else if (this.getClusterConfiguration(properties, connectionDetails) != null) {
            return Mode.CLUSTER;
        } else {
            return this.getMasterReplicaConfiguration(connectionDetails) != null ? Mode.MASTER_REPLICA : Mode.STANDALONE;
        }
    }

    static enum Mode {
        STANDALONE,
        CLUSTER,
        MASTER_REPLICA,
        SENTINEL;

        private Mode() {
        }
    }
}
