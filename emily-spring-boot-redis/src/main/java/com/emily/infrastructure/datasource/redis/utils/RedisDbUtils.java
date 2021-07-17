package com.emily.infrastructure.datasource.redis.utils;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.context.ioc.IOCContext;
import com.emily.infrastructure.datasource.redis.RedisDataSourceProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: spring-parent
 * @description: Redis数据源
 * @author: Emily
 * @create: 2021/07/11
 */
public class RedisDbUtils {
    /**
     * 字符串前缀
     */
    private static final String PREFIX_STRING = "S";
    /**
     * RestTemplate对象前缀
     */
    private static final String PREFIX_REST = "R";
    /**
     * StringRedisTemplate对象缓存
     */
    private static final Map<String, StringRedisTemplate> stringCache = new ConcurrentHashMap<>();
    /**
     * RedisTemplate对象缓存
     */
    private static final Map<String, RedisTemplate> restCache = new ConcurrentHashMap<>();

    /**
     * 获取Redis默认字符串模板
     *
     * @return
     */
    public static StringRedisTemplate getStringRedisTemplate() {
        return getStringRedisTemplate(RedisDataSourceProperties.DEFAULT_CONFIG);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param redisMark 数据源标识
     * @return
     */
    public static StringRedisTemplate getStringRedisTemplate(String redisMark) {
        /**
         * 获取缓存key
         */
        String key = getStringRedisTemplateBeanName(redisMark);
        if (stringCache.containsKey(key)) {
            return stringCache.get(key);
        }
        if (!IOCContext.containsBean(key)) {
            throw new BusinessException(AppHttpStatus.DATABASE_EXCEPTION.getStatus(), "Redis数据库标识对应的数据库不存在");
        }
        StringRedisTemplate stringRedisTemplate = IOCContext.getBean(key, StringRedisTemplate.class);
        stringCache.put(key, stringRedisTemplate);
        return stringRedisTemplate;
    }

    /**
     * 获取Redis默认字符串模板
     *
     * @return
     */
    public static RedisTemplate getRedisTemplate() {
        return getStringRedisTemplate(RedisDataSourceProperties.DEFAULT_CONFIG);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param redisMark 数据源标识
     * @return
     */
    public static RedisTemplate getRedisTemplate(String redisMark) {
        /**
         * 获取缓存key
         */
        String key = getRedisTemplateBeanName(redisMark);
        if (restCache.containsKey(key)) {
            return restCache.get(key);
        }
        if (!IOCContext.containsBean(key)) {
            throw new BusinessException(AppHttpStatus.DATABASE_EXCEPTION.getStatus(), "Redis数据库标识对应的数据库不存在");
        }
        RedisTemplate redisTemplate = IOCContext.getBean(key, RedisTemplate.class);
        restCache.put(key, redisTemplate);
        return redisTemplate;
    }

    /**
     * 获取StringRedisTemplate对象bean名称
     *
     * @param redisMark
     * @return
     */
    public static String getStringRedisTemplateBeanName(String redisMark) {
        if (Objects.isNull(redisMark)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "Redis数据库标识不可为空");
        }
        return MessageFormat.format("{0}_{1}", PREFIX_STRING, redisMark);
    }

    /**
     * 获取RedisTemplate对象bean名称
     *
     * @param redisMark
     * @return
     */
    public static String getRedisTemplateBeanName(String redisMark) {
        if (Objects.isNull(redisMark)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "Redis数据库标识不可为空");
        }
        return MessageFormat.format("{0}_{1}", PREFIX_REST, redisMark);
    }


    /**
     * 获取Redis配置
     * @param properties
     * @return
     */
    public static RedisConfiguration createRedisConfiguration(RedisProperties properties) {
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
    private static final RedisStandaloneConfiguration getStandaloneConfig(RedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(properties.getUrl())) {
            ConnectionInfo connectionInfo = parseUrl(properties.getUrl());
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
    private static final RedisSentinelConfiguration getSentinelConfig(RedisProperties properties) {
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
    private static final RedisClusterConfiguration getClusterConfiguration(RedisProperties properties) {
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


    private static List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
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

    public static ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), MessageFormat.format("Redis地址{0}异常", url));
            }
            boolean useSsl = ("rediss".equals(scheme));
            String username = null;
            String password = null;
            if (uri.getUserInfo() != null) {
                String candidate = uri.getUserInfo();
                int index = candidate.indexOf(':');
                if (index >= 0) {
                    username = candidate.substring(0, index);
                    password = candidate.substring(index + 1);
                }
                else {
                    password = candidate;
                }
            }
            return new ConnectionInfo(uri, useSsl, username, password);
        }
        catch (URISyntaxException ex) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), MessageFormat.format("Redis地址{0}异常{1}", url, PrintExceptionInfo.printErrorInfo(ex)));
        }
    }

    public static class ConnectionInfo {

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

        public boolean isUseSsl() {
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
