package com.emily.infrastructure.validation.annotation;

import com.emily.infrastructure.validation.IsPrefixesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 校验前缀
 * 1. ElementType.ANNOTATION_TYPE 用户其它约束的约束注解
 * 2. ElementType.FIELD 受约束的属性字段
 * 3. ElementType.PARAMETER 用于受约束的方法和构造函数参数
 * 4. ElementType.METHOD 用于受约束的getter和受约束的方法返回值
 *
 * @author :  Emily
 * @since :  2023/12/24 1:35 PM
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsPrefixesValidator.class})
public @interface IsPrefixes {
    /**
     * 提示信息
     */
    String message() default "非法前缀";

    /**
     * 校验分组
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 校验前缀
     *
     * @return 前缀数组
     */
    String[] values() default {};
}
