package com.emily.infrastructure.autoconfigure.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsIncludeInt;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Objects;

/**
 * 校验是否包含指定值
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsIncludeIntValidator implements ConstraintValidator<IsIncludeInt, Object> {
    private int[] includes;

    @Override
    public void initialize(IsIncludeInt annotation) {
        includes = annotation.includes();
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
        if (Objects.isNull(value)) {
            return true;
        }
        try {
            if (value instanceof Integer) {
                return Arrays.stream(includes).anyMatch(i -> i == (int) value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
