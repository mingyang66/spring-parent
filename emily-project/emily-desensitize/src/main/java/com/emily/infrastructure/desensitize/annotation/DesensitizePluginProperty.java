package com.emily.infrastructure.desensitize.annotation;

import com.emily.infrastructure.desensitize.DesensitizeType;
import com.emily.infrastructure.desensitize.plugin.DesensitizePlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于插件的自定义脱敏注解，此注解的优先级高于任何其他注解，仅低于{@link DesensitizeNullProperty}
 *
 * @author Emily
 * @since Created in 2023/4/15 10:15 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizePluginProperty {
    /**
     * 指定处理字段脱敏处理的插件
     */
    Class<? extends DesensitizePlugin> value() default DesensitizePlugin.class;

    /**
     * 脱敏数据类型
     */
    DesensitizeType desensitizeType() default DesensitizeType.DEFAULT;
}
