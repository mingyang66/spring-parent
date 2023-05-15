package com.emily.infrastructure.common.date;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 日期工具类
 * @create: 2020/06/16
 */
public class DateUtils {
    /**
     * 字符串日期格式化
     *
     * @param str           字符串日期
     * @param sourcePattern 原始日期格式
     * @param targetPattern 目标格式化格式
     * @return 格式化后的日期
     */
    public static String format(String str, String sourcePattern, String targetPattern) {
        try {
            Date date = org.apache.commons.lang3.time.DateUtils.parseDate(str, sourcePattern);
            return DateFormatUtils.format(date, targetPattern);
        } catch (ParseException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 字符串日期格式化
     *
     * @param pattern 目标格式化格式
     * @return 格式化后的日期
     */
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 字符串日期格式化
     *
     * @param str     字符串日期
     * @param pattern 原始日期格式
     * @return 格式化后的日期
     */
    public static Date parse(String str, String pattern) {
        try {
            Date date = org.apache.commons.lang3.time.DateUtils.parseDate(str, pattern);
            return date;
        } catch (ParseException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 判定日期是否跨年
     *
     * @param str     字符串日期
     * @param pattern 日期格式
     * @return true:跨年 false：未跨年
     */
    public static boolean isCrossYear(String str, String pattern) {
        try {
            Date date = org.apache.commons.lang3.time.DateUtils.parseDate(str, pattern);
            if (org.apache.commons.lang3.time.DateUtils.truncatedEquals(new Date(), date, Calendar.YEAR)) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 判定日期是否跨年
     *
     * @param date 日期
     * @return true:跨年 false：未跨年
     */
    public static boolean isCrossYear(Date date) {
        if (org.apache.commons.lang3.time.DateUtils.truncatedEquals(new Date(), date, Calendar.YEAR)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 日期转换为星期几
     *
     * @param str     字符串日期
     * @param pattern 日期格式
     * @return 如：星期一、星期二
     */
    public static String dateToWeek(String str, String pattern) {
        try {
            String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
            Date date = org.apache.commons.lang3.time.DateUtils.parseDate(str, pattern);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            return weekDays[w];
        } catch (ParseException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 日期转换为星期几
     *
     * @param date 日期
     * @return 如：星期一、星期二
     */
    public static String dateToWeek(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDays[w];
    }

    /**
     * 获取指定日期的年份
     *
     * @param date
     * @return
     */
    public static long getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取指定日期的月份
     *
     * @param date
     * @return
     */
    public static long getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取指定日期的日
     *
     * @param date
     * @return
     */
    public static long getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定日期的小时
     *
     * @param date
     * @return
     */
    public static long getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取指定日期的分钟
     *
     * @param date
     * @return
     */
    public static long getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取指定日期的秒数
     *
     * @param date
     * @return
     */
    public static long getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取指定日期是一周中的第几天，从周日开始计算
     *
     * @param date
     * @return
     */
    public static long getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取指定日期是一个月的第几周
     *
     * @param date
     * @return
     */
    public static long getWeekOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 比较日期大小，firstDate大于等于secondDate 返回true,否则返回false
     *
     * @param firstDate  日期字符串
     * @param secondDate 日期字符串
     * @param format     日期格式
     * @return
     */
    public static boolean compareTo(String firstDate, String secondDate, String format) {
        try {
            Date first = org.apache.commons.lang3.time.DateUtils.parseDate(firstDate, format);
            Date second = org.apache.commons.lang3.time.DateUtils.parseDate(secondDate, format);
            if (first.compareTo(second) >= 0) {
                return true;
            }
            return false;
        } catch (ParseException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 比较日期大小，firstDate大于等于secondDate 返回true,否则返回false
     *
     * @param firstDate  日期字符串
     * @param secondDate 日期字符串
     * @return
     */
    public static boolean compareTo(Date firstDate, Date secondDate) {
        if (firstDate.compareTo(secondDate) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * 判定指定的字符串是否可以转换为日期格式
     *
     * @param str 日期字符串
     * @return true-可以转换 false-不可以转换
     */
    public static boolean tryParse(String str) {
        try {
            org.apache.commons.lang3.time.DateUtils.parseDate(str, DatePatternType.getAllPatterns());
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 判定指定的字符串是否可以转换成指定格式的日期
     *
     * @param str     字符串日期
     * @param pattern 日期格式
     * @return
     */
    public static boolean tryParse(String str, String pattern) {
        try {
            org.apache.commons.lang3.time.DateUtils.parseDate(str, pattern);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 将日期类型转换为数字类型
     *
     * @param date    日期
     * @param pattern 格式@{@link DatePatternType}
     * @return
     */
    public static Long dateToNum(Date date, String pattern) {
        Objects.requireNonNull(date, "非法数据");
        Objects.requireNonNull(pattern, "非法数据");
        return Long.valueOf(format(date, pattern));
    }

    /**
     * 将日期类型转换为数字类型
     *
     * @param localDate 日期
     * @param pattern   格式@{@link DatePatternType}
     * @return
     */
    public static Long dateToNum(LocalDate localDate, String pattern) {
        Objects.requireNonNull(localDate, "非法数据");
        Objects.requireNonNull(pattern, "非法数据");
        return Long.valueOf(localDate.format(DateTimeFormatter.ofPattern(pattern)));
    }

    /**
     * 将日期类型转换为数字类型
     *
     * @param localDateTime 日期
     * @param pattern       格式@{@link DatePatternType}
     * @return YYYYMMDD格式
     */
    public static Long dateToNum(LocalDateTime localDateTime, String pattern) {
        Objects.requireNonNull(localDateTime, "非法数据");
        Objects.requireNonNull(pattern, "非法数据");
        return Long.valueOf(localDateTime.format(DateTimeFormatter.ofPattern(pattern)));
    }

    /**
     * 将数字类型日期转换类Date类型
     *
     * @param date    日期数字
     * @param pattern 格式@{@link DatePatternType}
     * @return
     */
    public static Date numToDate(Long date, String pattern) {
        Objects.requireNonNull(date, "非法数据");
        Objects.requireNonNull(pattern, "非法数据");
        return parse(String.valueOf(date), pattern);
    }

    /**
     * 将数字类型日期转换类LocalDate类型
     *
     * @param date    日期数字
     * @param pattern 格式@{@link DatePatternType}
     * @return
     */
    public static LocalDate numToLocalDate(Long date, String pattern) {
        Objects.requireNonNull(date, "非法数据");
        Objects.requireNonNull(String.valueOf(date).length() > 8, "非法数据");
        return LocalDate.parse(String.valueOf(date), DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将数字类型日期转换类LocalDateTime类型
     *
     * @param date    日期数字
     * @param pattern 格式@{@link DatePatternType}
     * @return
     */
    public static LocalDateTime numToLocalDateTime(Long date, String pattern) {
        Objects.requireNonNull(date, "非法数据");
        Objects.requireNonNull(String.valueOf(date).length() < 10, "非法数据");
        return LocalDateTime.parse(String.valueOf(date), DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将字符串日期转换为数字格式
     *
     * @param str           字符串日期
     * @param sourcePattern 字符串日期原格式
     * @param targetPattern 目标数字格式
     * @return
     */
    public static Long strToNum(String str, String sourcePattern, String targetPattern) {
        Objects.requireNonNull(str, "非法数据");
        Objects.requireNonNull(sourcePattern, "非法数据");
        Objects.requireNonNull(targetPattern, "非法数据");
        Date date = parse(str, sourcePattern);
        return Long.valueOf(DateFormatUtils.format(date, targetPattern));
    }

    /**
     * 将字符串日期转换为数字格式
     *
     * @param date          字符串日期
     * @param sourcePattern 字符串日期原格式
     * @param targetPattern 目标数字格式
     * @return
     */
    public static String numToStr(Long date, String sourcePattern, String targetPattern) {
        Objects.requireNonNull(date, "非法数据");
        Objects.requireNonNull(sourcePattern, "非法数据");
        Objects.requireNonNull(targetPattern, "非法数据");
        Date dateStr = parse(String.valueOf(date), sourcePattern);
        return DateFormatUtils.format(dateStr, targetPattern);
    }

    /**
     * 计算两个日期的时间间隔
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static Duration between(LocalDateTime startDate, LocalDateTime endDate) {
        Objects.requireNonNull(startDate, "非法数据");
        Objects.requireNonNull(endDate, "非法数据");
        return Duration.between(startDate, endDate);
    }

    /**
     * 获取指定日期的月份的第一天
     *
     * @param localDate 日期
     * @return
     */
    public static LocalDate firstDayOfMonth(LocalDate localDate) {
        Objects.requireNonNull(localDate, "非法数据");
        return localDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取指定日期的月份的第一天
     *
     * @param localDate 日期
     * @param month     向前推 month>0 向后推<0
     * @return
     */
    public static LocalDate firstDayOfMonth(LocalDate localDate, int month) {
        Objects.requireNonNull(localDate, "非法数据");
        if (month == 0) {
            return localDate.with(TemporalAdjusters.firstDayOfMonth());
        } else if (month < 0) {
            return localDate.minusMonths(-month).with(TemporalAdjusters.firstDayOfMonth());
        } else {
            return localDate.plusMonths(month).with(TemporalAdjusters.firstDayOfMonth());
        }
    }

    /**
     * 获取指定日期的月份的第一天
     *
     * @param localDateTime 日期
     * @return
     */
    public static LocalDate firstDayOfMonth(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "非法数据");
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
        Objects.requireNonNull(localDateTime, "非法数据");
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
     * @param localDate 日期
     * @return
     */
    public static LocalDate lastDayOfMonth(LocalDate localDate) {
        Objects.requireNonNull(localDate, "非法数据");
        return localDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取指定日期的月份的最后一天
     *
     * @param localDate 日期
     * @param month     向前推 month>0 向后推<0
     * @return
     */
    public static LocalDate lastDayOfMonth(LocalDate localDate, int month) {
        Objects.requireNonNull(localDate, "非法数据");
        if (month == 0) {
            return localDate.with(TemporalAdjusters.lastDayOfMonth());
        } else if (month < 0) {
            return localDate.minusMonths(-month).with(TemporalAdjusters.lastDayOfMonth());
        } else {
            return localDate.plusMonths(month).with(TemporalAdjusters.lastDayOfMonth());
        }
    }

    /**
     * 获取指定日期的月份的最后一天
     *
     * @param localDateTime 日期
     * @return
     */
    public static LocalDate lastDayOfMonth(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "非法数据");
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
        Objects.requireNonNull(localDateTime, "非法数据");
        if (month == 0) {
            return localDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
        } else if (month < 0) {
            return localDateTime.minusMonths(-month).with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
        } else {
            return localDateTime.plusMonths(month).with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
        }
    }

    /**
     * 获取今天剩余的时间
     *
     * @return
     */
    public static Duration getRemainTimeOfDay() {
        return getRemainTimeOfDay(LocalDateTime.now());
    }

    /**
     * 计算指定时间所在天剩余的时间
     *
     * @param localDateTime 指定日期
     * @return
     */
    public static Duration getRemainTimeOfDay(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "非法数据");
        LocalDateTime lastTime = localDateTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return Duration.between(localDateTime, lastTime);
    }

    /**
     * 获取今日所在月份剩余的天数
     *
     * @return
     */
    public static long getRemainDayOfMonth() {
        return getRemainDayOfMonth(LocalDateTime.now());
    }

    /**
     * 获取日期所在月份剩余的天数
     *
     * @param localDateTime 日期
     * @return
     */
    public static long getRemainDayOfMonth(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "非法数据");
        LocalDateTime lastDayOfMonth = localDateTime.with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfMonth.getDayOfMonth() - localDateTime.getDayOfMonth();
    }

    /**
     * 获取今年还剩的天数
     *
     * @return
     */
    public static long getRemainDayOfYear() {
        return getRemainDayOfYear(LocalDateTime.now());
    }

    /**
     * 获取指定日期所在年份剩余的天数
     *
     * @param localDateTime 日期
     * @return
     */
    public static long getRemainDayOfYear(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "非法数据");
        LocalDateTime lastDayOfYear = localDateTime.with(TemporalAdjusters.lastDayOfYear());
        return lastDayOfYear.getDayOfYear() - localDateTime.getDayOfYear();
    }

    /**
     * 将LocalDateTime转为Date
     *
     * @param localDateTime 日期类型
     * @return Date日期对象
     */
    public static Date asDate(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "非法数据");
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将LocalDateTime转为Date
     *
     * @param localDate 日期类型
     * @return Date日期对象
     */
    public static Date asDate(LocalDate localDate) {
        Objects.requireNonNull(localDate, "非法数据");
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
