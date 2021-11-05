package com.emily.infrastructure.datasource.redis.factory;

import com.emily.infrastructure.datasource.redis.entity.ConnectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(RedisDbConfigurationFactory.class);

    /**
     * 获取Redis配置
     *
     * @param properties
     * @return
     */
    public RedisConfiguration createRedisConfiguration(RedisProperties properties) {

        Assert.notNull(properties, "RedisProperties must not be null");

        if (getSentinelConfig(properties) != null) {
            return getSentinelConfig(properties);
        }
        if (getClusterConfiguration(properties) != null) {
            return getClusterConfiguration(properties);
        }
        return getStandaloneConfig(properties);
    }

    /**
     * 创建单机配置
     */
    private final RedisStandaloneConfiguration getStandaloneConfig(RedisProperties properties) {

        Assert.notNull(properties, "RedisProperties must not be null");

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(properties.getUrl())) {
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
        logger.info("初始化Redis单机连接配置-{}:{}", config.getHostName(), config.getPort());
        return config;
    }

    /**
     * 创建哨兵配置RedisSentinelConfiguration
     */
    private final RedisSentinelConfiguration getSentinelConfig(RedisProperties properties) {

        Assert.notNull(properties, "RedisProperties must not be null");

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
                logger.info("初始化Redis哨兵连接配置-{}", node);
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
