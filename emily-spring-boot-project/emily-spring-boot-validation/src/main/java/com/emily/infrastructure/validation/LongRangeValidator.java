package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.LongRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * long 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class LongRangeValidator implements ConstraintValidator<LongRange, Long> {
    private long min;
    private long max;

    @Override
    public void initialize(LongRange annotation) {
        min = annotation.min();
        max = annotation.max();
    }

    /**
     * 校验方法
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return true if the object is valid, false otherwise
     */
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) return true; // 结合@NotNull处理空值
        return value >= min && value <= max;
    }

}
