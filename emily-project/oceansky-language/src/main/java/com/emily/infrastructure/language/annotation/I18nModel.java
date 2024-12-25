package com.emily.infrastructure.language.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注实体类是否需要进行多语言解析
 *
 * @author Emily
 * @since Created in 2023/4/15 10:15 PM
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nModel {
}
