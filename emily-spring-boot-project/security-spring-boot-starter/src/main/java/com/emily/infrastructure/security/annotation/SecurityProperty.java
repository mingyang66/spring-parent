package com.emily.infrastructure.security.annotation;

import com.emily.infrastructure.security.plugin.BasePlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记实体类的字段需要加密
 *
 * @author :  Emily
 * @since :  2025/2/7 下午7:42
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityProperty {
    /**
     * 指定插件类
     *
     * @return 插件类
     */
    Class<? extends BasePlugin> value();
}
