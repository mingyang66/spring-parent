package com.emily.infrastructure.language.i18n.annotation;

import com.emily.infrastructure.language.i18n.plugin.I18nPlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于自定义插件方式多语言翻译，此注解的优先级高于任何其他注解；
 *
 * @author Emily
 * @since Created in 2023/4/15 10:15 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nPluginProperty {
    /**
     * 指定处理字段多语言处理的插件
     */
    Class<? extends I18nPlugin> value() default I18nPlugin.class;
}
