package com.emily.infrastructure.date;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 日期工具类
 * @create: 2020/06/16
 */
public class DateComputeUtils {
    /**
     * 获取指定日期的月份的第一天
     *
     * @param localDateTime 日期
     * @return 获取指定日期对应的第一天的日期对象
     */
    public static LocalDate firstDayOfMonth(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return localDateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
    }

    /**
     * 获取指定日期的月份的第一天
     *
     * @param localDateTime 日期
     * @param month         向前推 month>0 向后推<0
     * @return
     */
    public static LocalDate firstDayOfMonth(LocalDateTime localDateTime, int month) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (month == 0) {
            return localDateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        } else if (month < 0) {
            return localDateTime.minusMonths(-month).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        } else {
            return localDateTime.plusMonths(month).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        }
    }

    /**
     * 获取指定日期的月份的最后一天
     *
     * @param localDateTime 日期
     * @return
     */
    public static LocalDate lastDayOfMonth(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return localDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
    }

    /**
     * 获取指定日期的月份的最后一天
     *
     * @param localDateTime 日期
     * @param month         向前推 month>0 向后推<0
     * @return
     */
    public static LocalDate lastDayOfMonth(LocalDateTime localDateTime, int month) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (month == 0) {
            return localDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
        } else if (month < 0) {
            return localDateTime.minusMonths(-month).with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
        } else {
            return localDateTime.plusMonths(month).with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
        }
    }

    /**
     * 获取日期所在月份剩余的天数
     *
     * @param localDateTime 日期
     * @return 指定日期所在月份剩余天数
     */
    public static int getRemainDayOfMonth(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("非法参数");
        }
        LocalDateTime lastDayOfMonth = localDateTime.with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfMonth.getDayOfMonth() - localDateTime.getDayOfMonth();
    }

    /**
     * 获取指定日期所在年份剩余的天数
     *
     * @param localDateTime 日期
     * @return 指定日期所在年剩余天数
     */
    public static int getRemainDayOfYear(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("非法参数");
        }
        LocalDateTime lastDayOfYear = localDateTime.with(TemporalAdjusters.lastDayOfYear());
        return lastDayOfYear.getDayOfYear() - localDateTime.getDayOfYear();
    }

    /**
     * 计算两个日期的时间间隔，精度是秒、纳秒
     * ---------------------------------------------------------------
     * 格式转换
     * Duration fromChar1 = Duration.parse("P1DT1H10M10.5S");
     * Duration fromChar2 = Duration.parse("PT10M");
     * 采用ISO-8601时间格式。格式为：PnYnMnDTnHnMnS   （n为个数）
     * <p>
     * 例如：P1Y2M10DT2H30M15.03S
     * P：开始标记
     * 1Y：一年
     * 2M：两个月
     * 10D：十天
     * T：日期和时间的分割标记
     * 2H：两个小时
     * 30M：三十分钟
     * 15S：15.02秒
     * ---------------------------------------------------------------
     *
     * @param date1, 开始日期
     * @param date2  结束日期
     * @return date1-date2>0 返回正数，否则返回负数
     */
    public static Duration between(Temporal date1, Temporal date2) {
        if (date1 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (date2 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return Duration.between(date2, date1);
    }

    /**
     * 计算两个日期的时间间隔，精度为年 月 日
     * ---------------------------------------------------------------
     * 格式转换：
     * "P2Y"             -- Period.ofYears(2)
     * "P3M"             -- Period.ofMonths(3)
     * "P4W"             -- Period.ofWeeks(4)
     * "P5D"             -- Period.ofDays(5)
     * "P1Y2M3D"         -- Period.of(1, 2, 3)
     * "P1Y2M3W4D"       -- Period.of(1, 2, 25)
     * "P-1Y2M"          -- Period.of(-1, 2, 0)
     * "-P1Y2M"          -- Period.of(-1, -2, 0)
     * P：开始符，表示period（即：表示年月日）；
     * Y：year；
     * M：month；
     * W：week；
     * D：day
     * ---------------------------------------------------------------
     *
     * @param date1 日期对象
     * @param date2 日期对象
     * @return ddate1-date2>0 返回正数，否则返回负数
     */
    public static Period between(LocalDate date1, LocalDate date2) {
        if (date1 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (date2 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return Period.between(date2, date1);
    }
}
