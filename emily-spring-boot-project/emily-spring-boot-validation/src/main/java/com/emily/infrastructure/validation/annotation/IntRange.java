package com.emily.infrastructure.validation.annotation;

import com.emily.infrastructure.validation.IntRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 判断是否为int类型，如果为空则不校验
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
@Constraint(validatedBy = {IntRangeValidator.class})
public @interface IntRange {
    /**
     * 最小值
     */
    int min() default Integer.MIN_VALUE;

    /**
     * 最大值
     */
    int max() default Integer.MAX_VALUE;
    /**
     * 提示信息
     */
    String message() default "{jakarta.validation.constraints.IntRange.message}";

    /**
     * 校验分组
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
