package com.emily.infrastructure.autoconfigure.web.annotation;

import java.lang.annotation.*;

/**
 * API前缀注解，添加上该注解则自动加上指定前缀
 *
 * @author Emily
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPrefix {
    /**
     * 是否忽略前缀，默认false
     *
     * @return
     */
    boolean ignore() default false;
}
