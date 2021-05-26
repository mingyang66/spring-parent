package com.emily.infrastructure.common.utils.date;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.enums.DateFormatEnum;
import com.emily.infrastructure.common.exception.BusinessException;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 日期工具类
 * @create: 2020/06/16
 */
@SuppressWarnings("all")
public class DateUtils {
    /**
     * 字符串日期格式化
     *
     * @param dateStr        字符串日期
     * @param originalFormat 原始日期格式
     * @param nowFormat      目标格式化格式
     * @return 格式化后的日期
     */
    public static String formatDate(String dateStr, String originalFormat, String nowFormat) {
        try {
            Date date = org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, originalFormat);
            return DateFormatUtils.format(date, nowFormat);
        } catch (ParseException e) {
            throw new BusinessException(AppHttpStatus.DATE_TIME_EXCEPTION.getStatus(), "日期格式转换异常" + e);
        }
    }

    /**
     * 字符串日期格式化
     *
     * @param nowFormat 目标格式化格式
     * @return 格式化后的日期
     */
    public static String formatDate(Date date, String nowFormat) {
        return DateFormatUtils.format(date, nowFormat);
    }

    /**
     * 字符串日期格式化
     *
     * @param dateStr        字符串日期
     * @param originalFormat 原始日期格式
     * @return 格式化后的日期
     */
    public static Date parseDate(String dateStr, String originalFormat) {
        try {
            Date date = org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, originalFormat);
            return date;
        } catch (ParseException e) {
            throw new BusinessException(AppHttpStatus.DATE_TIME_EXCEPTION.getStatus(), "日期格式转换异常" + e);
        }
    }

    /**
     * 判定日期是否跨年
     *
     * @param dateStr
     * @param originalFormat 日期格式
     * @return true:跨年 false：未跨年
     */
    public static boolean isCrossYear(String dateStr, String originalFormat) {
        try {
            Date date = org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, originalFormat);
            if (org.apache.commons.lang3.time.DateUtils.truncatedEquals(new Date(), date, Calendar.YEAR)) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            throw new BusinessException(AppHttpStatus.DATE_TIME_EXCEPTION.getStatus(), "日期格式转换异常" + e);
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
     * @param dateStr 日期
     * @return 如：星期一、星期二
     */
    public static String dateToWeek(String dateStr, String format) {
        try {
            String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
            Date date = org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, format);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            return weekDays[w];
        } catch (ParseException e) {
            throw new BusinessException(AppHttpStatus.DATE_TIME_EXCEPTION.getStatus(), "日期格式转换异常" + e);
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
            throw new BusinessException(AppHttpStatus.DATE_TIME_EXCEPTION.getStatus(), AppHttpStatus.DATE_TIME_EXCEPTION.getMessage());
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
     * @param dateStr
     * @return
     */
    public static boolean tryParse(String dateStr) {
        try {
            org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, DateFormatEnum.getAllFormats());
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 判定指定的字符串是否可以转换成指定格式的日期
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static boolean tryParse(String dateStr, String format) {
        try {
            org.apache.commons.lang3.time.DateUtils.parseDate(dateStr, format);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 将日期类型转换为数字类型
     *
     * @param date   日期
     * @param format 格式@{@link DateFormatEnum}
     * @return
     */
    public static Long dateToNum(Date date, String format) {
        if (Objects.isNull(date)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "日期参数不可以为空");
        }
        return Long.valueOf(formatDate(date, format));
    }

    /**
     * 将日期类型转换为数字类型
     *
     * @param date   日期
     * @param format 格式@{@link DateFormatEnum}
     * @return
     */
    public static Long dateToNum(LocalDate date, String format) {
        if (Objects.isNull(date)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "日期参数不可以为空");
        }
        return Long.valueOf(date.format(DateTimeFormatter.ofPattern(format)));
    }

    /**
     * 将日期类型转换为数字类型
     *
     * @param date   日期
     * @param format 格式@{@link DateFormatEnum}
     * @return YYYYMMDD格式
     */
    public static Long dateToNum(LocalDateTime date, String format) {
        if (Objects.isNull(date)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "日期参数不可以为空");
        }
        return Long.valueOf(date.format(DateTimeFormatter.ofPattern(format)));
    }

    /**
     * 将数字类型日期转换类Date类型
     *
     * @param date   日期数字
     * @param format 格式@{@link DateFormatEnum}
     * @return
     */
    public static Date numToDate(Long date, String format) {
        if (Objects.isNull(date)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "日期参数不可以为空");
        }
        return parseDate(String.valueOf(date), format);
    }

    /**
     * 将数字类型日期转换类LocalDate类型
     *
     * @param date   日期数字
     * @param format 格式@{@link DateFormatEnum}
     * @return
     */
    public static LocalDate numToLocalDate(Long date, String format) {
        if (Objects.isNull(date)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "日期参数不可以为空");
        }
        return LocalDate.parse(String.valueOf(date), DateTimeFormatter.ofPattern(format));
    }

    /**
     * 将数字类型日期转换类LocalDateTime类型
     *
     * @param date   日期数字
     * @param format 格式@{@link DateFormatEnum}
     * @return
     */
    public static LocalDateTime numToLocalDateTime(Long date, String format) {
        if (Objects.isNull(date)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "日期参数不可以为空");
        }
        return LocalDateTime.parse(String.valueOf(date), DateTimeFormatter.ofPattern(format));
    }

    /**
     * 将字符串日期转换为数字格式
     *
     * @param dateStr      字符串日期
     * @param originFormat 字符串日期原格式
     * @param format       目标数字格式
     * @return
     */
    public static Long strToNum(String dateStr, String originFormat, String format) {
        if (Objects.isNull(dateStr)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "日期参数不可以为空");
        }
        if (Objects.isNull(originFormat)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "原日期格式参数不可以为空");
        }
        if (Objects.isNull(format)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "目标日期格式参数不可以为空");
        }
        Date date = parseDate(dateStr, originFormat);
        return Long.valueOf(DateFormatUtils.format(date, format));
    }

    /**
     * 将字符串日期转换为数字格式
     *
     * @param date         字符串日期
     * @param originFormat 字符串日期原格式
     * @param format       目标数字格式
     * @return
     */
    public static String numToStr(Long date, String originFormat, String format) {
        if (Objects.isNull(date)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "日期参数不可以为空");
        }
        if (Objects.isNull(originFormat)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "原日期格式参数不可以为空");
        }
        if (Objects.isNull(format)) {
            throw new BusinessException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "目标日期格式参数不可以为空");
        }
        Date dateStr = parseDate(String.valueOf(date), originFormat);
        return DateFormatUtils.format(dateStr, format);
    }
}
