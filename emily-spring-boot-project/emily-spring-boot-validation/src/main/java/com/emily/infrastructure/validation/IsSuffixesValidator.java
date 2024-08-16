package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.IsSuffixes;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * 后缀 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsSuffixesValidator implements ConstraintValidator<IsSuffixes, String> {
    private String[] values;

    @Override
    public void initialize(IsSuffixes annotation) {
        values = annotation.values();
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
            if (StringUtils.endsWithAny(value, values)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
