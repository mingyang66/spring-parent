package com.emily.infrastructure.datasource.redis.entity;

import com.emily.infrastructure.datasource.redis.exception.RedisUrlSyntaxException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @program: spring-parent
 * @description: Redis连接配置信息
 * @author: Emily
 * @create: 2021/11/06
 */
public class ConnectionInfo {
    private final URI uri;

    private final boolean useSsl;

    private final String username;

    private final String password;

    public ConnectionInfo(URI uri, boolean useSsl, String username, String password) {
        this.uri = uri;
        this.useSsl = useSsl;
        this.username = username;
        this.password = password;
    }

    public boolean isUseSsl() {
        return this.useSsl;
    }

    public String getHostName() {
        return this.uri.getHost();
    }

    public int getPort() {
        return this.uri.getPort();
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public static ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisUrlSyntaxException(url);
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
                } else {
                    password = candidate;
                }
            }
            return new ConnectionInfo(uri, useSsl, username, password);
        } catch (URISyntaxException ex) {
            throw new RedisUrlSyntaxException(url, ex);
        }
    }
}
