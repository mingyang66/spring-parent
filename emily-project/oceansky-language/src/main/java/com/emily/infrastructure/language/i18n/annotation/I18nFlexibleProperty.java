package com.emily.infrastructure.language.i18n.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在由两个字段灵活定义可以传入不同的key、value
 * <pre>{@code
 * @I18nModel
 * public class FlexibleField {
 *     @I18nFlexibleProperty(value = {"username", "email"}, target = "key2")
 *     private String key1;
 *     private String key2;
 * }
 * }</pre>
 *
 * @author Emily
 * @since Created in 2023/4/15 11:17 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nFlexibleProperty {
    /**
     * 期望做翻译的key值集合
     */
    String[] value() default {};

    /**
     * 期望做翻译具体字段对应的值
     */
    String target();
}
