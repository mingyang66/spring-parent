package com.emily.infrastructure.transfer.rest.annotation;


import java.lang.annotation.*;

/**
 * http单个请求超时时间设置
 *
 * @author Emily
 * @since 4.1.3
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TargetHttpTimeout {
    /**
     * 读取超时时间，默认：5000毫秒
     *
     * @return 连接超时时间
     */
    int readTimeout() default 5000;

    /**
     * 连接超时时间，默认：10000毫秒
     *
     * @return 连接超时时间
     */
    int connectTimeout() default 10000;
}

