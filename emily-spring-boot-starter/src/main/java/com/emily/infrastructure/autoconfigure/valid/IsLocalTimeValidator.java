package com.emily.infrastructure.autoconfigure.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsLocalTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义LocalDate校验注解
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsLocalTimeValidator implements ConstraintValidator<IsLocalTime, String> {
    private boolean required;
    private String pattern;

    @Override
    public void initialize(IsLocalTime annotation) {
        required = annotation.required();
        pattern = annotation.pattern();
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
        // 格式校验
        if (StringUtils.isBlank(pattern)) {
            return false;
        }
        try {
            // 格式校验
            LocalTime.parse(value, DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
