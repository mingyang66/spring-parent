package com.sgrain.boot.autoconfigure.ratelimit.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 接口访问频率限制
 * @create: 2020/03/23
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiRateLimit {
    /**
     * 限流控制开关
     */
    boolean enable() default true;

    /**
     * 单位时间漏铜的阀值，默认10
     */
    long permits() default 10;

    /**
     * 时间
     */
    long time() default 1;

    /**
     * 单位，默认是秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
