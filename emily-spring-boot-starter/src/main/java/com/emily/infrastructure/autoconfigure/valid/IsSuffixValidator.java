package com.emily.infrastructure.autoconfigure.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsSuffix;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 后缀 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsSuffixValidator implements ConstraintValidator<IsSuffix, String> {
    private boolean required;
    private String[] suffixes;

    @Override
    public void initialize(IsSuffix annotation) {
        required = annotation.required();
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
        // 必传校验
        if (required) {
            if (StringUtils.isBlank(value)) {
                return false;
            }
        } else {
            if (StringUtils.isBlank(value)) {
                return true;
            }
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
