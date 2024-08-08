package com.emily.infrastructure.redis.connection;

/**
 * @author Emily
 * @since 2021/11/05
 */
public class RedisDbUrlSyntaxException extends RuntimeException {
    private final String url;

    public RedisDbUrlSyntaxException(String url, Exception cause) {
        super(buildMessage(url), cause);
        this.url = url;
    }

    public RedisDbUrlSyntaxException(String url) {
        super(buildMessage(url));
        this.url = url;
    }

    String getUrl() {
        return this.url;
    }

    private static String buildMessage(String url) {
        return "Invalid Redis URL '" + url + "'";
    }
}