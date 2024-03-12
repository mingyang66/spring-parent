package com.emily.infrastructure.autoconfigure.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsDouble;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * double 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsDoubleValidator implements ConstraintValidator<IsDouble, String> {
    private double min;
    private double max;
    private List<String> allows;

    @Override
    public void initialize(IsDouble annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
        this.allows = Arrays.asList(annotation.allows());
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
            long count = allows.stream().filter(f -> f.equals(value)).count();
            if (count > 0) {
                return true;
            }
            // 格式校验
            double v = Double.parseDouble(value);
            if (v < min || v > max) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
