package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.SuffixCheck;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * 后缀 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class SuffixCheckValidator implements ConstraintValidator<SuffixCheck, String> {
    private String[] suffix;

    @Override
    public void initialize(SuffixCheck annotation) {
        suffix = annotation.value();
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
        return StringUtils.endsWithAny(value, suffix);
    }

}
