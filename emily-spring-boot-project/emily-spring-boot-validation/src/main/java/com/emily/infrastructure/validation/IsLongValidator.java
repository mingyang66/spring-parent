package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.IsLong;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * long 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsLongValidator implements ConstraintValidator<IsLong, String> {
    private long min;
    private long max;
    private List<String> values;

    @Override
    public void initialize(IsLong annotation) {
        min = annotation.min();
        max = annotation.max();
        this.values = Arrays.asList(annotation.values());
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
        if (StringUtils.isBlank(value)) {
            return true;
        }
        try {
            long count = values.stream().filter(f -> f.equals(value)).count();
            if (count > 0) {
                return true;
            }
            // 格式校验
            long v = Long.parseLong(value);
            if (v < min || v > max) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
