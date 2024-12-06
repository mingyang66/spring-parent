package com.emily.infrastructure.desensitize.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法返回值脱敏注解标记：
 * 1. 标记在类上，类的所有方法都会被拦截处理
 * 2. 标记在方法上，只有当前方法才会被处理
 *
 * @author Emily
 * @since :  Created in 2022/11/4 11:23 上午
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizeOperation {
    /**
     * 需要移除的外层包装类
     */
    Class<?> removePackClass() default void.class;
}
