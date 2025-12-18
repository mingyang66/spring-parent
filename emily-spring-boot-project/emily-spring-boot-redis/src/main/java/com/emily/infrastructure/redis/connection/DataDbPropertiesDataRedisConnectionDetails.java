package com.emily.infrastructure.redis.connection;

import com.emily.infrastructure.redis.RedisProperties;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.data.redis.autoconfigure.DataRedisConnectionDetails;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * org.springframework.boot.data.redis.autoconfigure.PropertiesDataRedisConnectionDetails
 *
 * @author :  Emily
 * @since :  2023/9/25 211:58 PM
 */
public class DataDbPropertiesDataRedisConnectionDetails implements DataRedisConnectionDetails {

    private final RedisProperties properties;
    private final @Nullable SslBundles sslBundles;

    public DataDbPropertiesDataRedisConnectionDetails(RedisProperties properties, SslBundles sslBundles) {
        this.properties = properties;
        this.sslBundles = sslBundles;
    }

    public @Nullable String getUsername() {
        DataDbRedisUrl redisUrl = this.getRedisUrl();
        return redisUrl != null ? redisUrl.credentials().username() : this.properties.getUsername();
    }

    public @Nullable String getPassword() {
        DataDbRedisUrl redisUrl = this.getRedisUrl();
        return redisUrl != null ? redisUrl.credentials().password() : this.properties.getPassword();
    }

    public @Nullable SslBundle getSslBundle() {
        if (!this.properties.getSsl().isEnabled()) {
            return null;
        } else {
            String bundleName = this.properties.getSsl().getBundle();
            if (StringUtils.hasLength(bundleName)) {
                Assert.notNull(this.sslBundles, "SSL bundle name has been set but no SSL bundles found in context");
                return this.sslBundles.getBundle(bundleName);
            } else {
                return SslBundle.systemDefault();
            }
        }
    }

    public DataRedisConnectionDetails.Standalone getStandalone() {
        DataDbRedisUrl redisUrl = this.getRedisUrl();
        return redisUrl != null ? Standalone.of(redisUrl.uri().getHost(), redisUrl.uri().getPort(), redisUrl.database()) : Standalone.of(this.properties.getHost(), this.properties.getPort(), this.properties.getDatabase());
    }

    public DataRedisConnectionDetails.@Nullable Sentinel getSentinel() {
        RedisProperties.Sentinel sentinel = this.properties.getSentinel();
        return sentinel != null ? new PropertiesSentinel(this.getStandalone().getDatabase(), sentinel) : null;
    }

    public DataRedisConnectionDetails.@Nullable Cluster getCluster() {
        RedisProperties.Cluster cluster = this.properties.getCluster();
        return cluster != null ? new PropertiesCluster(cluster) : null;
    }

    public DataRedisConnectionDetails.@Nullable MasterReplica getMasterReplica() {
        RedisProperties.Masterreplica masterreplica = this.properties.getMasterreplica();
        return masterreplica != null ? new PropertiesMasterReplica(masterreplica) : null;
    }


    private @Nullable DataDbRedisUrl getRedisUrl() {
        return DataDbRedisUrl.of(this.properties.getUrl());
    }

    private List<DataRedisConnectionDetails.Node> asNodes(@Nullable List<String> nodes) {
        return nodes == null ? Collections.emptyList() : nodes.stream().map(this::asNode).toList();
    }

    private DataRedisConnectionDetails.Node asNode(String node) {
        int portSeparatorIndex = node.lastIndexOf(58);
        String host = node.substring(0, portSeparatorIndex);
        int port = Integer.parseInt(node.substring(portSeparatorIndex + 1));
        return new DataRedisConnectionDetails.Node(host, port);
    }

    private class PropertiesSentinel implements DataRedisConnectionDetails.Sentinel {
        private final int database;
        private final RedisProperties.Sentinel properties;

        PropertiesSentinel(int database, RedisProperties.Sentinel properties) {
            this.database = database;
            this.properties = properties;
        }

        public int getDatabase() {
            return this.database;
        }

        public String getMaster() {
            String master = this.properties.getMaster();
            Assert.state(master != null, "'master' must not be null");
            return master;
        }

        public List<DataRedisConnectionDetails.Node> getNodes() {
            return DataDbPropertiesDataRedisConnectionDetails.this.asNodes(this.properties.getNodes());
        }

        public @Nullable String getUsername() {
            return this.properties.getUsername();
        }

        public @Nullable String getPassword() {
            return this.properties.getPassword();
        }
    }

    private class PropertiesCluster implements DataRedisConnectionDetails.Cluster {
        private final List<DataRedisConnectionDetails.Node> nodes;

        PropertiesCluster(RedisProperties.Cluster properties) {
            this.nodes = DataDbPropertiesDataRedisConnectionDetails.this.asNodes(properties.getNodes());
        }

        public List<DataRedisConnectionDetails.Node> getNodes() {
            return this.nodes;
        }
    }

    private class PropertiesMasterReplica implements DataRedisConnectionDetails.MasterReplica {
        private final List<DataRedisConnectionDetails.Node> nodes;

        PropertiesMasterReplica(RedisProperties.Masterreplica properties) {
            this.nodes = DataDbPropertiesDataRedisConnectionDetails.this.asNodes(properties.getNodes());
        }

        public List<DataRedisConnectionDetails.Node> getNodes() {
            return this.nodes;
        }
    }
}
