package com.emily.infrastructure.rateLimiter.annotation;

import java.lang.annotation.*;

/**
 * @author :  Emily
 * @since :  2024/8/30 上午9:37
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RateLimiter {
    /**
     * 缓存key前缀
     */
    String prefix();

    /**
     * 缓存过期时间，单位：秒
     */
    long expired();
}
