package com.emily.infrastructure.autoconfigure.valid.annotation;

import com.emily.infrastructure.autoconfigure.valid.IsIncludeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验是值否为符合指定日期格式要求
 *
 * @author :  Emily
 * @since :  2023/12/24 1:35 PM
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsIncludeValidator.class})
public @interface IsInclude {
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
     * 值是否必须，true为必须，false为非必须
     */
    boolean required() default true;

    String[] includeString() default {};

    int[] includeInt() default {};

    long[] includeLong() default {};

    double[] includeDouble() default {};
}
