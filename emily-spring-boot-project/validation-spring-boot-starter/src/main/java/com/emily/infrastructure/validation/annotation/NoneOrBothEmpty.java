package com.emily.infrastructure.validation.annotation;

import com.emily.infrastructure.validation.NoneOrBothEmptyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段对应的值全部为空或全部不为空
 *
 * @author :  Emily
 * @since :  2025/7/19 下午6:01
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoneOrBothEmptyValidator.class)
public @interface NoneOrBothEmpty {
    String[] value() default {};

    String message() default "字段对应的值全部为空或全部不为空";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
