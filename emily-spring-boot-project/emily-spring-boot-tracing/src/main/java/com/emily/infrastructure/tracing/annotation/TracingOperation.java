package com.emily.infrastructure.tracing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 全链路日志追踪注解，标记继承上下文属性注解
 * 1. 适用于非servlet上下文中；
 * 2. servlet上下文即也可以使用，不影响原功能；
 *
 * @author Emily
 * @since :  Created in 2022/11/4 11:23 上午
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TracingOperation {
    /**
     * 描述：标记继承上下文属性注解，此属性无其他作用
     */
    String message() default "标记继承上下文属性注解";

}
