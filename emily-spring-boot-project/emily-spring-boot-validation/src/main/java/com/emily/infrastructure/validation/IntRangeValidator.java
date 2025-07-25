package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.IntRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * int 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IntRangeValidator implements ConstraintValidator<IntRange, Object> {
    private int min;
    private int max;
    private boolean minInclusive;
    private boolean maxInclusive;

    @Override
    public void initialize(IntRange annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
        this.minInclusive = annotation.minInclusive();
        this.maxInclusive = annotation.maxInclusive();
    }

    /**
     * 校验方法
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return true if the object is valid, false otherwise
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true; // 结合@NotNull处理空值
        try {
            int d = Integer.parseInt(value.toString());
            boolean minValid = minInclusive ? d >= min : d > min;
            boolean maxValid = maxInclusive ? d <= max : d < max;
            return minValid && maxValid;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
