package com.emily.infrastructure.autoconfigure.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * 自定义LocalDate校验注解
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsListIncludeValidator implements ConstraintValidator<IsInclude, Object> {
    private String[] includeString;
    private int[] includeInt;
    private long[] includeLong;
    private double[] includeDouble;

    @Override
    public void initialize(IsInclude annotation) {
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
        // 空校验
        if (value == null) {
            return false;
        }
        if (value instanceof String) {
            return Arrays.asList(includeString).contains((String) value);
        } else if (value instanceof Integer) {
            return Arrays.stream(includeInt).anyMatch(i -> i == (int) value);
        } else if (value instanceof Long) {
            return Arrays.stream(includeLong).anyMatch(i -> i == (long) value);
        } else if (value instanceof Double) {
            return Arrays.stream(includeDouble).anyMatch(i -> i == (double) value);
        }
        return false;
    }
}
