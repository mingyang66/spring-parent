package com.emily.infrastructure.datasource.redis.exception;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/11/05
 */
public class RedisUrlSyntaxException extends RuntimeException {

    private final String url;

    public RedisUrlSyntaxException(String url, Exception cause) {
        super(buildMessage(url), cause);
        this.url = url;
    }

    public RedisUrlSyntaxException(String url) {
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