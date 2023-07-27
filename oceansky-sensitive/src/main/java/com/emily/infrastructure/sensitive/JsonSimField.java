package com.emily.infrastructure.sensitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义jackson注解，标注在属性上，字段必须是字符串类型
 *
 * @author  Emily
 * @since :  Created in 2022/7/19 5:22 下午
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSimField {
    /**
     * 脱敏类型，见枚举类型{@link SensitiveType}
     *
     * @return 脱敏类型
     */
    SensitiveType value() default SensitiveType.DEFAULT;
}
