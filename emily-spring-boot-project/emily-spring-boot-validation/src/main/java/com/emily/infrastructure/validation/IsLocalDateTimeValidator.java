package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.IsLocalDateTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义LocalDateTime校验注解
 *
 * @author :  Emily
 * @since :  2023/12/24 1:32 PM
 */
public class IsLocalDateTimeValidator implements ConstraintValidator<IsLocalDateTime, String> {
    private String pattern;

    @Override
    public void initialize(IsLocalDateTime annotation) {
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
        if (StringUtils.isBlank(value)) {
            return true;
        }
        // 格式校验
        if (StringUtils.isBlank(pattern)) {
            return false;
        }
        try {
            // 格式校验
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
