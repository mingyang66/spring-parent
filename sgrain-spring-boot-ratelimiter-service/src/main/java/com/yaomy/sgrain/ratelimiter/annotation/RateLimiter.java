package com.yaomy.sgrain.ratelimiter.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 接口访问频率限制
 * @author: 姚明洋
 * @create: 2020/03/23
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {
    /**
     * 单位时间请求接口的数量限制，默认100
     */
    double permits() default 20;

    /**
     * 参数之中带指定的参数时限制
     */
    String[] name() default {};

    /**
     * 单位，默认是秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
