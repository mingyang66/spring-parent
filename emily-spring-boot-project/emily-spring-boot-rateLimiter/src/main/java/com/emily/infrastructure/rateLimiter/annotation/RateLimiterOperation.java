package com.emily.infrastructure.rateLimiter.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 *
 * @author :  Emily
 * @since :  2024/8/30 上午9:37
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RateLimiterOperation {
    /**
     * 缓存key前缀
     */
    String value();

    /**
     * 缓存过期时间，单位：秒
     */
    long timeout() default -1;

    /**
     * 超时时间 单位：秒
     */
    TimeUnit timeunit() default TimeUnit.SECONDS;

    /**
     * 最大限流次数，默认：10
     */
    int threshold() default 10;

    /**
     * 超过限流次数提醒消息
     */
    String message() default "您已触发访问限制，请等待几分钟后再试。";
}
