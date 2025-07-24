package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.NumericRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * long 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class NumericRangeValidator implements ConstraintValidator<NumericRange, String> {
    private long min;
    private long max;

    @Override
    public void initialize(NumericRange annotation) {
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
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        try {
            long num = Long.parseLong(value);
            return num >= min && num <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
