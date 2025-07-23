package com.emily.infrastructure.validation.annotation;

import com.emily.infrastructure.validation.NotBothEmptyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段对应的值不可以全部为空
 *
 * @author :  Emily
 * @since :  2025/7/19 下午6:01
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBothEmptyValidator.class)
public @interface NotBothEmpty {
    String[] value() default {};

    String message() default "字段对应的值不可以全部为空";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
