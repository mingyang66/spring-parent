package com.emily.infrastructure.sensitive.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Json脱敏标记，必须指定此标记，否则脱敏无效
 *
 * @author Emily
 * @since :  Created in 2022/11/4 11:23 上午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSensitive {
}
