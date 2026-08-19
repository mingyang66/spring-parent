package com.emily.infrastructure.validation;

import com.emily.infrastructure.validation.annotation.IsBeforeEndDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    @Override
    public void initialize(IsBeforeEndDate annotation) {
        startField = annotation.startField();
        endField = annotation.endField();
        pattern = annotation.pattern();
        inclusive = annotation.inclusive();
        dateType = annotation.dateType();
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
            if (startDate == null || endDate == null) {
                return true;
            }

            if (startDate instanceof LocalDate startDate1 && endDate instanceof LocalDate endDate1) {
                if (inclusive) {
                    return !startDate1.isAfter(endDate1);
                } else {
                    return startDate1.isBefore(endDate1);
                }
            } else if (startDate instanceof LocalDateTime startDate1 && endDate instanceof LocalDateTime endDate1) {
                if (inclusive) {
                    return !startDate1.isAfter(endDate1);
                } else {
                    return startDate1.isBefore(endDate1);
                }
            } else if (startDate instanceof LocalTime startDate1 && endDate instanceof LocalTime endDate1) {
                if (inclusive) {
                    return !startDate1.isAfter(endDate1);
                } else {
                    return startDate1.isBefore(endDate1);
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

                    LocalDate localStartDate = LocalDate.parse(startDate.toString(), DateTimeFormatter.ofPattern(pattern));
                    LocalDate localEndDate = LocalDate.parse(endDate.toString(), DateTimeFormatter.ofPattern(pattern));
                    if (inclusive) {
                        return !localStartDate.isAfter(localEndDate);
                    } else {
                        return localStartDate.isBefore(localEndDate);
                    }
                } else if (IsBeforeEndDate.DateType.DATE_TIME.equals(dateType)) {
                    LocalDateTime localStartDate = LocalDateTime.parse(startDate.toString(), DateTimeFormatter.ofPattern(pattern));
                    LocalDateTime localEndDate = LocalDateTime.parse(endDate.toString(), DateTimeFormatter.ofPattern(pattern));
                    if (inclusive) {
                        return !localStartDate.isAfter(localEndDate);
                    } else {
                        return localStartDate.isBefore(localEndDate);
                    }
                } else if (IsBeforeEndDate.DateType.TIME.equals(dateType)) {
                    LocalTime localStartDate = LocalTime.parse(startDate.toString(), DateTimeFormatter.ofPattern(pattern));
                    LocalTime localEndDate = LocalTime.parse(endDate.toString(), DateTimeFormatter.ofPattern(pattern));
                    if (inclusive) {
                        return !localStartDate.isAfter(localEndDate);
                    } else {
                        return localStartDate.isBefore(localEndDate);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
