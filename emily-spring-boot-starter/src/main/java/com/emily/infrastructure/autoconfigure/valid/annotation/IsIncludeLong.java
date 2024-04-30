package com.emily.infrastructure.autoconfigure.valid.annotation;

import com.emily.infrastructure.autoconfigure.valid.IsIncludeLongValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验是否包含指定值
 * 1. ElementType.ANNOTATION_TYPE 用户其它约束的约束注解
 * 2. ElementType.FIELD 受约束的属性字段
 * 3. ElementType.PARAMETER 用于受约束的方法和构造函数参数
 * 4. ElementType.METHOD 用于受约束的getter和受约束的方法返回值
 *
 * @author :  Emily
 * @since :  2023/12/24 1:35 PM
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsIncludeLongValidator.class})
public @interface IsIncludeLong {
    /**
     * 提示信息
     */
    String message() default "必须为指定值";

    /**
     * 校验分组
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 包含的值
     *
     * @return 包含的值
     */
    long[] values() default {};

}
