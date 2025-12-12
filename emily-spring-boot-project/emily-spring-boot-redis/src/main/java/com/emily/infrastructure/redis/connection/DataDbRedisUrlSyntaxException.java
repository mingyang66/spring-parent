package com.emily.infrastructure.redis.connection;

/**
 * @author :  Emily
 * @since :  2025/11/28 下午4:16
 */
public class DataDbRedisUrlSyntaxException extends RuntimeException {
    private final String url;

    DataDbRedisUrlSyntaxException(String url, Exception cause) {
        super(buildMessage(url), cause);
        this.url = url;
    }

    DataDbRedisUrlSyntaxException(String url) {
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
