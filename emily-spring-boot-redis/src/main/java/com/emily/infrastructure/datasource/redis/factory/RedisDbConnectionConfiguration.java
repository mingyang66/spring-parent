package com.emily.infrastructure.datasource.redis.factory;

import com.emily.infrastructure.datasource.redis.entity.ConnectionInfo;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: spring-parent
 * @description: RedisConfiguration工厂配置类  RedisConnectionConfiguration
 * @author: Emily
 * @create: 2021/07/11
 */
public class RedisDbConnectionConfiguration {

    private RedisProperties properties;

    public RedisDbConnectionConfiguration(RedisProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取Redis配置
     *
     * @return
     */
    public RedisConfiguration createRedisConfiguration() {

        if (getSentinelConfig() != null) {
            return getSentinelConfig();
        }
        if (getClusterConfiguration() != null) {
            return getClusterConfiguration();
        }
        return getStandaloneConfig();
    }

    /**
     * 创建单机配置
     */
    protected final RedisStandaloneConfiguration getStandaloneConfig() {

        Assert.notNull(properties, "RedisProperties must not be null");

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(this.properties.getUrl())) {
            ConnectionInfo connectionInfo = ConnectionInfo.parseUrl(properties.getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setUsername(connectionInfo.getUsername());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        } else {
            config.setHostName(properties.getHost());
            config.setPort(properties.getPort());
            config.setUsername(properties.getUsername());
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        config.setDatabase(properties.getDatabase());
        return config;
    }

    /**
     * 创建哨兵配置RedisSentinelConfiguration
     */
    private final RedisSentinelConfiguration getSentinelConfig() {

        Assert.notNull(properties, "RedisProperties must not be null");

        RedisProperties.Sentinel sentinelProperties = properties.getSentinel();
        if (sentinelProperties != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(sentinelProperties.getMaster());
            config.setSentinels(createSentinels(sentinelProperties));
            config.setUsername(properties.getUsername());
            if (properties.getPassword() != null) {
                config.setPassword(RedisPassword.of(properties.getPassword()));
            }
            if (sentinelProperties.getPassword() != null) {
                config.setSentinelPassword(RedisPassword.of(sentinelProperties.getPassword()));
            }
            config.setDatabase(properties.getDatabase());
            return config;
        }
        return null;
    }

    /**
     * 创建RedisClusterConfiguration集群配置
     */
    private final RedisClusterConfiguration getClusterConfiguration() {

        Assert.notNull(properties, "RedisProperties must not be null");

        if (properties.getCluster() == null) {
            return null;
        }
        RedisProperties.Cluster clusterProperties = properties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        config.setUsername(properties.getUsername());
        if (properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        return config;
    }

    /**
     * 哨兵节点配置转换
     *
     * @param sentinel 哨兵配置对象
     * @return
     */
    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
            }
        }
        return nodes;
    }
}
