package com.emily.infrastructure.autoconfigure.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsIncludeLong;
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
public class IsIncludeLongValidator implements ConstraintValidator<IsIncludeLong, Object> {
    private boolean required;
    private long[] includes;

    @Override
    public void initialize(IsIncludeLong annotation) {
        required = annotation.required();
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
        // 必传校验
        if (required) {
            if (Objects.isNull(value)) {
                return false;
            }
        } else {
            if (Objects.isNull(value)) {
                return true;
            }
        }
        try {
            if (value instanceof Long) {
                return Arrays.stream(includes).anyMatch(i -> i == (long) value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
