package com.emily.infrastructure.autoconfigure.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsSuffixes;
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
    private String[] suffixes;

    @Override
    public void initialize(IsSuffixes annotation) {
        suffixes = annotation.suffixes();
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
            if (StringUtils.endsWithAny(value, suffixes)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
