package com.emily.infrastructure.language.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注需要做多语言支持的字段
 *
 * @author Emily
 * @since Created in 2023/4/15 5:17 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nProperty {
}
