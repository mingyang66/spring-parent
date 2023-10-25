package com.emily.infrastructure.redis.connection;

import com.emily.infrastructure.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2023/9/25 211:58 PM
 */
public class PropertiesRedisDbConnectionDetails implements RedisConnectionDetails {

    private final RedisProperties properties;

    public PropertiesRedisDbConnectionDetails(RedisProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getUsername() {
        if (this.properties.getUrl() != null) {
            RedisDbConnectionConfiguration.ConnectionInfo connectionInfo = connectionInfo(this.properties.getUrl());
            return connectionInfo.getUsername();
        }
        return this.properties.getUsername();
    }

    @Override
    public String getPassword() {
        if (this.properties.getUrl() != null) {
            RedisDbConnectionConfiguration.ConnectionInfo connectionInfo = connectionInfo(this.properties.getUrl());
            return connectionInfo.getPassword();
        }
        return this.properties.getPassword();
    }

    @Override
    public Standalone getStandalone() {
        if (this.properties.getUrl() != null) {
            RedisDbConnectionConfiguration.ConnectionInfo connectionInfo = connectionInfo(this.properties.getUrl());
            return Standalone.of(connectionInfo.getUri().getHost(), connectionInfo.getUri().getPort(),
                    this.properties.getDatabase());
        }
        return Standalone.of(this.properties.getHost(), this.properties.getPort(), this.properties.getDatabase());
    }

    private RedisDbConnectionConfiguration.ConnectionInfo connectionInfo(String url) {
        return (url != null) ? RedisDbConnectionConfiguration.parseUrl(url) : null;
    }

    @Override
    public Sentinel getSentinel() {
        org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel sentinel = this.properties
                .getSentinel();
        if (sentinel == null) {
            return null;
        }
        return new Sentinel() {

            @Override
            public int getDatabase() {
                return PropertiesRedisDbConnectionDetails.this.properties.getDatabase();
            }

            @Override
            public String getMaster() {
                return sentinel.getMaster();
            }

            @Override
            public List<Node> getNodes() {
                return sentinel.getNodes().stream().map(PropertiesRedisDbConnectionDetails.this::asNode).toList();
            }

            @Override
            public String getUsername() {
                return sentinel.getUsername();
            }

            @Override
            public String getPassword() {
                return sentinel.getPassword();
            }

        };
    }

    @Override
    public Cluster getCluster() {
        RedisProperties.Cluster cluster = this.properties.getCluster();
        List<Node> nodes = (cluster != null) ? cluster.getNodes().stream().map(this::asNode).toList() : null;
        return (nodes != null) ? () -> nodes : null;
    }

    private Node asNode(String node) {
        String[] components = node.split(":");
        return new Node(components[0], Integer.parseInt(components[1]));
    }

}
