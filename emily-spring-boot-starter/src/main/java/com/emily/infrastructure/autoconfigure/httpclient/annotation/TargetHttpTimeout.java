package com.emily.infrastructure.autoconfigure.httpclient.annotation;


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
     * 读取超时时间，默认：-1
     */
    int readTimeout() default -1;

    /**
     * 连接超时时间，默认：-1
     */
    int connectTimeout() default -1;
}

