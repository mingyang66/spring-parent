package com.sgrain.boot.autoconfigure.web.annotation;

import java.lang.annotation.*;

/**
 * API前缀注解，添加上该注解则自动加上指定前缀
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPrefix {
}
