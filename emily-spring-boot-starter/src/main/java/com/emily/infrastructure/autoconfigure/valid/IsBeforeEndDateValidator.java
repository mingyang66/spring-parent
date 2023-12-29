package com.emily.infrastructure.autoconfigure.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsBeforeEndDate;
import com.emily.infrastructure.date.DateCompareUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 验证一个日期是否小于等于另外一个日期
 *
 * @author :  Emily
 * @since :  2023/12/28 7:43 PM
 */
public class IsBeforeEndDateValidator implements ConstraintValidator<IsBeforeEndDate, Object> {
    private String startField;
    private String endField;
    private String pattern;
    private boolean inclusive;
    private IsBeforeEndDate.DateType dateType;
    private boolean required;

    @Override
    public void initialize(IsBeforeEndDate annotation) {
        startField = annotation.startField();
        endField = annotation.endField();
        pattern = annotation.pattern();
        inclusive = annotation.inclusive();
        dateType = annotation.dateType();
        required = annotation.required();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        try {
            Object startDate = null;
            Object endDate = null;
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().equals(startField)) {
                    startDate = field.get(obj);
                } else if (field.getName().equals(endField)) {
                    endDate = field.get(obj);
                }
            }
            if (required) {
                if (startDate == null || endDate == null) {
                    return false;
                }
            } else {
                if (startDate == null || endDate == null) {
                    return true;
                }
            }

            if (startDate instanceof LocalDate && endDate instanceof LocalDate) {
                if (inclusive) {
                    return DateCompareUtils.compareTo((LocalDate) startDate, (LocalDate) endDate) <= 0;
                } else {
                    return DateCompareUtils.compareTo((LocalDate) startDate, (LocalDate) endDate) < 0;
                }
            } else if (startDate instanceof LocalDateTime && endDate instanceof LocalDateTime) {
                if (inclusive) {
                    return DateCompareUtils.compareTo((LocalDateTime) startDate, (LocalDateTime) endDate) <= 0;
                } else {
                    return DateCompareUtils.compareTo((LocalDateTime) startDate, (LocalDateTime) endDate) < 0;
                }
            } else if (startDate instanceof LocalTime && endDate instanceof LocalTime) {
                if (inclusive) {
                    return DateCompareUtils.compareTo((LocalTime) startDate, (LocalTime) endDate) <= 0;
                } else {
                    return DateCompareUtils.compareTo((LocalTime) startDate, (LocalTime) endDate) < 0;
                }
            } else if (startDate instanceof Integer && endDate instanceof Integer) {
                if (inclusive) {
                    return ((Integer) startDate).compareTo((Integer) endDate) <= 0;
                } else {
                    return ((Integer) startDate).compareTo((Integer) endDate) < 0;
                }
            } else if (startDate instanceof Long && endDate instanceof Long) {
                if (inclusive) {
                    return ((Long) startDate).compareTo((Long) endDate) <= 0;
                } else {
                    return ((Long) startDate).compareTo((Long) endDate) < 0;
                }
            } else if (startDate instanceof String && endDate instanceof String) {
                if (StringUtils.isBlank(pattern)) {
                    return false;
                }
                if (IsBeforeEndDate.DateType.DATE.equals(dateType)) {
                    LocalDate localStartDate = DateConvertUtils.toLocalDate(startDate.toString(), pattern);
                    LocalDate localEndDate = DateConvertUtils.toLocalDate(endDate.toString(), pattern);
                    if (inclusive) {
                        return DateCompareUtils.compareTo(localStartDate, localEndDate) <= 0;
                    } else {
                        return DateCompareUtils.compareTo(localStartDate, localEndDate) < 0;
                    }
                } else if (IsBeforeEndDate.DateType.DATE_TIME.equals(dateType)) {
                    LocalDateTime localStartDate = DateConvertUtils.toLocalDateTime(startDate.toString(), pattern);
                    LocalDateTime localEndDate = DateConvertUtils.toLocalDateTime(endDate.toString(), pattern);
                    if (inclusive) {
                        return DateCompareUtils.compareTo(localStartDate, localEndDate) <= 0;
                    } else {
                        return DateCompareUtils.compareTo(localStartDate, localEndDate) < 0;
                    }
                } else if (IsBeforeEndDate.DateType.TIME.equals(dateType)) {
                    LocalTime localStartDate = DateConvertUtils.toLocalTime(startDate.toString(), pattern);
                    LocalTime localEndDate = DateConvertUtils.toLocalTime(endDate.toString(), pattern);
                    if (inclusive) {
                        return DateCompareUtils.compareTo(localStartDate, localEndDate) <= 0;
                    } else {
                        return DateCompareUtils.compareTo(localStartDate, localEndDate) < 0;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
