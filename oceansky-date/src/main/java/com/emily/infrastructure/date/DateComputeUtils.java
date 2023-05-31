package com.emily.infrastructure.date;

import java.time.*;
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
        DateAssert.illegalArgument(localDateTime, "非法参数");
        return localDateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
    }

    /**
     * 获取指定日期的月份的第一天
     *
     * @param localDateTime 日期
     * @param month         向前推 month>0 向后推<0
     * @return 第一天
     */
    public static LocalDate firstDayOfMonth(LocalDateTime localDateTime, int month) {
        DateAssert.illegalArgument(localDateTime, "非法参数");
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
     * @return 最后一天的日期对象
     */
    public static LocalDate lastDayOfMonth(LocalDateTime localDateTime) {
        DateAssert.illegalArgument(localDateTime, "非法参数");
        return localDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
    }

    /**
     * 获取指定日期的月份的最后一天
     *
     * @param localDateTime 日期
     * @param month         向前推 month>0 向后推<0
     * @return 一个月的最后一天日期对象
     */
    public static LocalDate lastDayOfMonth(LocalDateTime localDateTime, int month) {
        DateAssert.illegalArgument(localDateTime, "非法参数");
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
        DateAssert.illegalArgument(localDateTime, "非法参数");
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
        DateAssert.illegalArgument(localDateTime, "非法参数");
        LocalDateTime lastDayOfYear = localDateTime.with(TemporalAdjusters.lastDayOfYear());
        return lastDayOfYear.getDayOfYear() - localDateTime.getDayOfYear();
    }

    /**
     * 获取今天剩余时间
     *
     * @return 剩余时间对象
     */
    public static Duration getRemainTimeOfDay() {
        return DateComputeUtils.between(LocalDate.now().plusDays(1).atStartOfDay(), LocalDateTime.now());
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
        DateAssert.illegalArgument(date1, "非法参数");
        DateAssert.illegalArgument(date2, "非法参数");
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
        DateAssert.illegalArgument(date1, "非法参数");
        DateAssert.illegalArgument(date2, "非法参数");
        return Period.between(date2, date1);
    }

    /**
     * 两个对象相减得到毫秒
     *
     * @param instant1 时间对象
     * @param instant2 时间对象
     * @return 毫秒
     */
    public static long minusMillis(Instant instant1, Instant instant2) {
        DateAssert.illegalArgument(instant1, "非法参数");
        DateAssert.illegalArgument(instant2, "非法参数");
        return instant1.minusMillis(instant2.toEpochMilli()).toEpochMilli();
    }

    /**
     * 判定指定的年份是否是闰年
     * -------------------------------------------------------------------
     * 闰年是指公历中含有366天的年份，通常是每4年一次，但有例外。闰年的2月份有29天，而平年只有28天。在公历中，从公元前45年到公元3200年，闰年的规则为：
     * 能被4整除但不能被100整除的年份是闰年（如2004年就是闰年）；
     * 能被400整除的年份也是闰年（如2000年是闰年，1900年不是闰年）；
     * 闰年的出现是为了弥补平年中一年中多出来的约0.242199天（即365.242199天）的差距。
     * -------------------------------------------------------------------
     * @param year 年份
     * @return true-是，false-否
     */
    public static boolean isLeapYear(int year) {
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                return year % 400 == 0;
            } else {
                return true;
            }
        }
        return false;
    }
}
