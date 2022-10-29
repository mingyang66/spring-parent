package com.emily.infrastructure.common.sensitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description :  自定义jackson注解，标注在属性上
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 5:22 下午
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSensitive {
    SensitiveType value() default SensitiveType.DEFAULT;
}
