package com.emily.infrastructure.sensitive.annotation;

import com.emily.infrastructure.sensitive.SensitiveType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义jackson注解，标注在属性上，实例如下：
 *
 * @author Emily
 * @since :  Created in 2022/7/19 5:22 下午
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonMapField {
    /**
     * 要隐藏的参数key名称
     *
     * @return 复杂类型字段名
     */
    String[] fieldKeys() default {};

    /**
     * 脱敏类型，见枚举类型{@link SensitiveType}
     *
     * @return 脱敏类型
     */
    SensitiveType[] types() default {};
}
