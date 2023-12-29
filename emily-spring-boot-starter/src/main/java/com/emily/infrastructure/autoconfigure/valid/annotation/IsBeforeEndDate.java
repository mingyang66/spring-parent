package com.emily.infrastructure.autoconfigure.valid.annotation;

import com.emily.infrastructure.autoconfigure.valid.IsBeforeEndDateValidator;
import com.emily.infrastructure.date.DatePatternInfo;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证开始日期是否小于等于结束日期，字段类型支持String、Int、Long、LocalDate、LocalDateTime、LocalTime
 * 1. 如果字段类型是String类型，则使用案例如下：
 * <pre>{@code
 * @IsBeforeEndDate(startField = "startDate", endField = "endDate", dateType = IsBeforeEndDate.DateType.DATE, pattern = DatePatternInfo.YYYY_MM_DD, message = "日期大小不符合要求", inclusive = true)
 * public class TaskForm {
 *     private String startDate;
 *
 *     private String endDate;
 *  }
 * }</pre>
 * 2. 如果字段类型是Int类型，则使用案例如下：
 * <pre>{@code
 *  @IsBeforeEndDate(startField = "startDate", endField = "endDate", message = "日期大小不符合要求", inclusive = false)
 * public class TaskForm {
 *     private int startDate;
 *
 *     private int endDate;
 * }
 * }</pre>
 * 3. 如果字段类型是LocalDate类型，则使用案例如下：
 * <pre>{@code
 * @IsBeforeEndDate(startField = "startDate", endField = "endDate", message = "日期大小不符合要求", inclusive = false)
 * public class TaskForm {
 *     private LocalDate startDate;
 *
 *     private LocalDate endDate;
 * }
 * }</pre>
 *
 * @author :  Emilu
 * @since :  2023/12/28 7:43 PM
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsBeforeEndDateValidator.class)
public @interface IsBeforeEndDate {
    String message() default "End date must be after start date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    boolean required() default true;

    String startField();

    String endField();

    /**
     * 日期格式，只有字段为String类型时有效
     *
     * @return 格式化字符串
     */
    String pattern() default DatePatternInfo.YYYY_MM_DD;

    /**
     * 日期类型
     *
     * @return 类型
     */
    DateType dateType() default DateType.DATE;

    /**
     * 默认：true
     *
     * @return true：<= endField，false：< endField
     */
    boolean inclusive() default true;

    enum DateType {
        DATE_TIME,
        DATE,
        TIME;
    }
}

