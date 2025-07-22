package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.NoneOrBothEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字段对应的值全部为空或全部不为空
 *
 * @author :  Emily
 * @since :  2025/7/19 下午6:01
 */
public class NoneOrBothEmptyValidator implements ConstraintValidator<NoneOrBothEmpty, Object> {
    private String[] fieldNames;

    @Override
    public void initialize(NoneOrBothEmpty annotation) {
        fieldNames = annotation.value();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        try {
            if (fieldNames == null || fieldNames.length == 0) {
                return true;
            }
            List<Boolean> list = new ArrayList<>();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (Arrays.stream(fieldNames).anyMatch(s -> StringUtils.equals(s, field.getName()))) {
                    Object value = field.get(obj);
                    list.add(ObjectUtils.isNotEmpty(value));
                }
            }
            return list.stream().noneMatch(Boolean.FALSE::equals);
        } catch (Exception ex) {
            return false;
        }
    }
}

