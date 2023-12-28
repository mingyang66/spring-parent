package com.emily.infrastructure.autoconfigure.valid.annotation;

import com.emily.infrastructure.autoconfigure.valid.IsIntValidator;
import com.emily.infrastructure.autoconfigure.valid.IsPrefixValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 校验前缀
 *
 * @author :  Emily
 * @since :  2023/12/24 1:35 PM
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsPrefixValidator.class})
public @interface IsPrefix {
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
     * 值是否必须，true为必须，false为非必须
     */
    boolean required() default true;

    String[] prefixes() default {};
}
