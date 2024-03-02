package com.emily.infrastructure.autoconfigure.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsInt;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * int 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsIntValidator implements ConstraintValidator<IsInt, String> {
    private int min;
    private int max;

    @Override
    public void initialize(IsInt annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
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
            // 格式校验
            int v = Integer.parseInt(value);
            if (v < min || v > max) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
