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
 * @description: RedisConfiguration工厂配置类
 * @author: Emily
 * @create: 2021/07/11
 */
public class RedisDbConfigurationFactory {

    private static final RedisDbConfigurationFactory INSTANCE = new RedisDbConfigurationFactory();
    /**
     * 获取Redis配置
     * @param properties
     * @return
     */
    public static RedisConfiguration createRedisConfiguration(RedisProperties properties) {
        if (INSTANCE.getSentinelConfig(properties) != null) {
            return INSTANCE.getSentinelConfig(properties);
        }
        if (INSTANCE.getClusterConfiguration(properties) != null) {
            return INSTANCE.getClusterConfiguration(properties);
        }
        return INSTANCE.getStandaloneConfig(properties);
    }

    /**
     * 创建单机配置
     */
    private final RedisStandaloneConfiguration getStandaloneConfig(RedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(properties.getUrl())) {
            ConnectionInfo connectionInfo = ConnectionInfo.parseUrl(properties.getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setUsername(connectionInfo.getUsername());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        }
        else {
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
    private final RedisSentinelConfiguration getSentinelConfig(RedisProperties properties) {
        RedisProperties.Sentinel sentinelProperties = properties.getSentinel();
        if (sentinelProperties != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            // Redis服务器名称
            config.master(sentinelProperties.getMaster());
            // 哨兵sentinel "host:port"节点配置
            config.setSentinels(createSentinels(sentinelProperties));
            // Redis服务器登录名
            config.setUsername(properties.getUsername());
            // Redis服务器登录密码
            if (properties.getPassword() != null) {
                config.setPassword(RedisPassword.of(properties.getPassword()));
            }
            // 哨兵sentinel进行身份验证的密码
            if (sentinelProperties.getPassword() != null) {
                config.setSentinelPassword(RedisPassword.of(sentinelProperties.getPassword()));
            }
            // 连接工厂使用的数据库索引
            config.setDatabase(properties.getDatabase());
            return config;
        }
        return null;
    }

    /**
     * 创建RedisClusterConfiguration集群配置
     */
    private final RedisClusterConfiguration getClusterConfiguration(RedisProperties properties) {
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


    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            }
            catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
            }
        }
        return nodes;
    }
}
