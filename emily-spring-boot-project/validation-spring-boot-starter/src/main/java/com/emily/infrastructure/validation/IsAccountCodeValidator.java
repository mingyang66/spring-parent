package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.IsAccountCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 账号 校验
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsAccountCodeValidator implements ConstraintValidator<IsAccountCode, String> {
    private Class<?> type;
    private int minLength;
    private int maxLength;
    private List<String> prefixes;
    private List<String> suffixes;

    @Override
    public void initialize(IsAccountCode annotation) {
        type = annotation.type();
        minLength = annotation.minLength();
        maxLength = annotation.maxLength();
        prefixes = Arrays.asList(annotation.prefixes());
        suffixes = Arrays.asList(annotation.suffixes());
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
        // 类型验证
        try {
            if (type.equals(Long.class)) {
                Long.parseLong(value);
                return true;
            } else if (type.equals(Integer.class)) {
                Integer.parseInt(value);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        // 长度验证
        if (minLength > 0 && value.length() < minLength) {
            return false;
        }
        if (maxLength > 0 && value.length() > maxLength) {
            return false;
        }
        // 前缀后缀验证
        if (!prefixes.isEmpty() && prefixes.stream().noneMatch(prefix -> StringUtils.startsWith(value, prefix))) {
            return false;
        }
        return suffixes.isEmpty() || suffixes.stream().anyMatch(suffix -> StringUtils.endsWith(value, suffix));
    }

}
