package com.sgrain.boot.common.utils.date;

import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @program: spring-parent
 * @description: 日期工具类
 * @create: 2020/06/16
 */
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
            throw new BusinessException(AppHttpStatus.DATE_PARSE_EXCEPTION.getStatus(), "日期格式转换异常" + e);
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
            throw new BusinessException(AppHttpStatus.DATE_PARSE_EXCEPTION.getStatus(), "日期格式转换异常" + e);
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
            throw new BusinessException(AppHttpStatus.DATE_PARSE_EXCEPTION.getStatus(), "日期格式转换异常" + e);
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
            throw new BusinessException(AppHttpStatus.DATE_PARSE_EXCEPTION.getStatus(), "日期格式转换异常" + e);
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
}
