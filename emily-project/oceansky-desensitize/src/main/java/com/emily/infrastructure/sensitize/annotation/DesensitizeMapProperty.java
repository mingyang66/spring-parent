package com.emily.infrastructure.sensitize.annotation;

import com.emily.infrastructure.sensitize.DesensitizeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体类Map集合字符串对象脱敏处理，优先级高于{@link DesensitizeProperty}：
 * <pre>
 *     {@code
 *     @DesensitizeMapProperty(value = {"password", "username"}, desensitizeType = {DesensitizeType.DEFAULT, DesensitizeType.USERNAME})
 *     private Map<String, String> params = new HashMap<>();
 *     }
 * </pre>
 * 1. Map的key必须为字符串，否则忽略
 * 2. Map的value值必须为字符串或对象类型；
 *
 * @author Emily
 * @since :  Created in 2022/7/19 10:22 下午
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizeMapProperty {
    /**
     * 要隐藏的参数key名称
     *
     * @return 复杂类型字段名
     */
    String[] value() default {};

    /**
     * 脱敏类型，见枚举类型{@link DesensitizeType}
     *
     * @return 脱敏类型
     */
    DesensitizeType[] desensitizeType() default {};
}
