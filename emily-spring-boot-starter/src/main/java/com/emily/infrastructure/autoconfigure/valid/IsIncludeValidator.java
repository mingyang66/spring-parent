package com.emily.infrastructure.autoconfigure.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsInclude;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Objects;

/**
 * 自定义LocalDate校验注解
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsIncludeValidator implements ConstraintValidator<IsInclude, Object> {
    private boolean required;
    private String[] includeString;
    private int[] includeInt;
    private long[] includeLong;
    private double[] includeDouble;

    @Override
    public void initialize(IsInclude annotation) {
        required = annotation.required();
        includeString = annotation.includeString();
        includeInt = annotation.includeInt();
        includeLong = annotation.includeLong();
        includeDouble = annotation.includeDouble();
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
            if (value instanceof String) {
                return Arrays.asList(includeString).contains((String) value);
            } else if (value instanceof Integer) {
                return Arrays.stream(includeInt).anyMatch(i -> i == (int) value);
            } else if (value instanceof Long) {
                return Arrays.stream(includeLong).anyMatch(i -> i == (long) value);
            } else if (value instanceof Double) {
                return Arrays.stream(includeDouble).anyMatch(i -> i == (double) value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
