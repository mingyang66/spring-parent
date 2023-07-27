package com.emily.infrastructure.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类：
 * ----------------------------------------------
 * 日期字符串-日期字符串
 * 字符串-Date
 * <p>
 * 字符串-LocalDateTime
 * Date-LocalDateTime
 * LocalDate-LocalDateTime
 * <p>
 * 字符串-LocalDate
 * Date-LocalDate
 * LocalDateTime-LocalDate
 * <p>
 * 字符串-LocalTime
 * Date-LocalTime
 * LocalDateTime-LocalTime
 * <p>
 * 日期-Int
 * ----------------------------------------------
 *
 * @author Emily
 * @since 2020/06/16
 */
public class DateConvertUtils {
    /**
     * 字符串日期格式化
     *
     * @param str           字符串日期
     * @param sourcePattern 原始日期格式
     * @param targetPattern 目标格式化格式
     * @return 格式化后的日期
     */
    public static String format(String str, String sourcePattern, String targetPattern) {
        DateAssert.illegalArgument(str, "非法参数");
        DateAssert.illegalArgument(sourcePattern, "非法参数");
        DateAssert.illegalArgument(targetPattern, "非法参数");
        DateFormat sdf = new SimpleDateFormat(targetPattern);
        return sdf.format(toDate(str, sourcePattern));
    }

    /**
     * 字符串日期格式化
     *
     * @param date    日期对象
     * @param pattern 目标格式化格式
     * @return 格式化后的日期
     */
    public static String format(Date date, String pattern) {
        DateAssert.illegalArgument(date, "非法参数");
        DateAssert.illegalArgument(pattern, "非法参数");
        DateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 日期对象转字符串
     *
     * @param date    日期对象
     * @param pattern 日期格式
     * @return 字符串日期
     */
    public static String format(LocalTime date, String pattern) {
        DateAssert.illegalArgument(date, "非法参数");
        DateAssert.illegalArgument(pattern, "非法参数");
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期对象转字符串
     *
     * @param date    日期对象
     * @param pattern 日期格式
     * @return 字符串日期
     */
    public static String format(LocalDate date, String pattern) {
        return format(date, pattern, ZoneId.systemDefault());
    }

    /**
     * 日期对象转字符串
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param date    日期对象
     * @param pattern 日期格式
     * @param zoneId  时区
     * @return 字符串日期
     */
    public static String format(LocalDate date, String pattern, ZoneId zoneId) {
        DateAssert.illegalArgument(date, "非法参数");
        DateAssert.illegalArgument(pattern, "非法参数");
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        return date.atStartOfDay().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期对象转字符串
     *
     * @param date    日期对象
     * @param pattern 日期格式
     * @return 字符串日期
     */
    public static String format(LocalDateTime date, String pattern) {
        return format(date, pattern, ZoneId.systemDefault());
    }

    /**
     * 日期对象转字符串
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param date    日期对象
     * @param pattern 日期格式
     * @param zoneId  时区
     * @return 字符串日期
     */
    public static String format(LocalDateTime date, String pattern, ZoneId zoneId) {
        DateAssert.illegalArgument(date, "非法参数");
        DateAssert.illegalArgument(pattern, "非法参数");
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        return date.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).format(DateTimeFormatter.ofPattern(pattern));
    }

    //----------------------------------------------------------Date----------------------------------------------------------------------------------

    /**
     * 字符串日期格式化
     *
     * @param str     字符串日期
     * @param pattern 日期格式
     * @return 格式化后的日期
     */
    public static Date toDate(String str, String pattern) {
        try {
            DateAssert.illegalArgument(str, "非法参数");
            DateAssert.illegalArgument(pattern, "非法参数");
            DateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException("非法参数");
        }
    }

    /**
     * 将LocalDateTime转为Date
     *
     * @param localDateTime 日期类型
     * @return Date日期对象
     */
    public static Date toDate(LocalDateTime localDateTime) {
        DateAssert.illegalArgument(localDateTime, "非法参数");
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将LocalDateTime转为Date
     *
     * @param localDate 日期类型
     * @return Date日期对象
     */
    public static Date toDate(LocalDate localDate) {
        DateAssert.illegalArgument(localDate, "非法参数");
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将毫秒转换为日期对象
     * Date date = new Date(0) 获取到的世界标准时间，即格林威治时间；
     * 如果要转换为中国时间，因为我们是在东八区，需加上八个小时，所以是1970-01-01 08:00:00
     *
     * @param milliseconds 毫秒，如：System.currentTimeMillis()  1685353612112L
     * @return 日期对象
     */
    public static Date toDate(long milliseconds) {
        return new Date(milliseconds);
    }
    //-----------------------------------------------------------LocalDateTime---------------------------------------------------------------------------------

    /**
     * 将LocalDate转换为LocalDateTime
     *
     * @param localDate 日期对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        return toLocalDateTime(localDate, ZoneId.systemDefault());
    }

    /**
     * 将LocalDate转换为LocalDateTime
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param localDate 日期对象
     * @param zoneId    时区
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(LocalDate localDate, ZoneId zoneId) {
        DateAssert.illegalArgument(localDate, "非法参数");
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        return localDate.atStartOfDay().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDateTime();
    }

    /**
     * 将Date数据类型转换为LocalDateTime
     *
     * @param date 日期对象
     * @return 转换后的LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return toLocalDateTime(date, ZoneId.systemDefault());
    }

    /**
     * 将Date数据类型转换为LocalDateTime
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param date   日期对象
     * @param zoneId 时区
     * @return 转换后的LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        DateAssert.illegalArgument(date, "非法参数");
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        return date.toInstant().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDateTime();
    }

    /**
     * 将字符串日期转换为LocalDateTime对象
     *
     * @param str     字符串日期
     * @param pattern 格式
     * @return 日期对象
     */
    public static LocalDateTime toLocalDateTime(String str, String pattern) {
        return toLocalDateTime(str, pattern, ZoneId.systemDefault());
    }

    /**
     * 将字符串日期转换为LocalDateTime对象
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param str     字符串日期
     * @param pattern 格式
     * @param zoneId  时区
     * @return 日期对象
     */
    public static LocalDateTime toLocalDateTime(String str, String pattern, ZoneId zoneId) {
        DateAssert.illegalArgument(str, "非法参数");
        DateAssert.illegalArgument(pattern, "非法参数");
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern)).atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDateTime();
    }

    /**
     * 将日期对象和时间对象拼接成一个日期对象
     *
     * @param date1 日期对象
     * @param date2 时间对象
     * @return 拼接后的时间对象
     */
    public static LocalDateTime toLocalDateTime(LocalDate date1, LocalTime date2) {
        return toLocalDateTime(date1, date2, ZoneId.systemDefault());
    }

    /**
     * 将日期对象和时间对象拼接成一个日期对象
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param date1  日期对象
     * @param date2  时间对象
     * @param zoneId 时区
     * @return 拼接后的时间对象
     */
    public static LocalDateTime toLocalDateTime(LocalDate date1, LocalTime date2, ZoneId zoneId) {
        DateAssert.illegalArgument(date1, "非法参数");
        DateAssert.illegalArgument(date2, "非法参数");
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        return LocalDateTime.of(date1, date2).atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDateTime();
    }

    /**
     * 将时间转换为另一个时区的时间对象
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param date1  日期对象
     * @param zoneId 时区对象
     * @return 转换后的日期对象
     */
    public static LocalDateTime toLocalDateTime(LocalDateTime date1, ZoneId zoneId) {
        DateAssert.illegalArgument(date1, "非法参数");
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        return date1.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDateTime();
    }

    /**
     * 将时间戳转换为日期对象
     *
     * @param milliseconds 时间戳，毫秒，如： System.currentTimeMillis()  1685353612112L
     * @return 日期对象
     */
    public static LocalDateTime toLocalDateTime(long milliseconds) {
        return toLocalDateTime(milliseconds, ZoneId.systemDefault());
    }

    /**
     * 将时间戳转换为日期对象
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * Instant instant = Instant.ofEpochMilli(0);
     * LocalDateTime localDateTime2 = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
     * 获取到的是世界标准时间，即格林威治时间；因为我们是在东八区，需加上八个小时，所以格式化后的时间是1970-01-01 08:00:00
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param milliseconds 时间戳，毫秒，如： System.currentTimeMillis()  1685353612112L
     * @param zoneId       时区
     * @return 日期对象
     */
    public static LocalDateTime toLocalDateTime(long milliseconds, ZoneId zoneId) {
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        Instant instant = Instant.ofEpochMilli(milliseconds);
        return LocalDateTime.ofInstant(instant, zoneId);
    }
    //-------------------------------------------------------------------LocalDate-------------------------------------------------------------------------

    /**
     * 将Date数据类型转换为LocalDate
     *
     * @param date 日期对象
     * @return 转换后的LocalDate对象
     */
    public static LocalDate toLocalDate(Date date) {
        return toLocalDate(date, ZoneId.systemDefault());
    }

    /**
     * 将Date数据类型转换为LocalDate
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param date   日期对象
     * @param zoneId 时区
     * @return 转换后的LocalDate对象
     */
    public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
        DateAssert.illegalArgument(date, "非法参数");
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        return date.toInstant().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDate();
    }

    /**
     * 将LocalDateTime 转 LocalDate
     *
     * @param localDateTime 日期对象
     * @return LocalDate日期对象
     */
    public static LocalDate toLocalDate(LocalDateTime localDateTime) {
        return toLocalDate(localDateTime, ZoneId.systemDefault());
    }

    /**
     * 将LocalDateTime 转 LocalDate
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param localDateTime 日期对象
     * @param zoneId        时区
     * @return LocalDate日期对象
     */
    public static LocalDate toLocalDate(LocalDateTime localDateTime, ZoneId zoneId) {
        DateAssert.illegalArgument(localDateTime, "非法参数");
        zoneId = DateAssert.requireElseGet(zoneId, ZoneId.systemDefault());
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDate();
    }

    /**
     * 将字符串日期转换为LocalDate对象
     *
     * @param str     字符串日期
     * @param pattern 格式
     * @return 日期对象
     */
    public static LocalDate toLocalDate(String str, String pattern) {
        return toLocalDate(str, pattern, ZoneId.systemDefault());
    }

    /**
     * 将字符串日期转换为LocalDate对象
     * --------------------------------------------------------------------------------------------
     * 示例说明：
     * <p>
     * 格林威治时区：ZoneId.of("UTC") 和 ZoneId.of("GMT")
     * <p>
     * 北京时间，东八区： ZoneId.of("Asia/Shanghai") 和 ZoneId.of("GMT+8")
     * Asia/Shanghai和GMT+8都是用于表示北京时间的方式，它们都代表同一个时区。但是在Java中，推荐使用"Asia/Shanghai"这个标识符来表示北京时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而GMT+8只是一个表示时差的简单方式，没有考虑到夏令时等复杂情况。
     * <p>
     * 美东时区：ZoneId.of("America/New_York") 和 ZoneId.of("US/Eastern")
     * America/New_York和US/Eastern都是表示美东时间的时区标识符，但是在Java中，推荐使用"America/New_York"这个标识符来表示美东时间。
     * 因为它更准确地反映了这个时区的历史和规则变化，可以更好地处理夏令时等问题。而US/Eastern是一个较旧的标识符，更容易导致时区错误
     * --------------------------------------------------------------------------------------------
     *
     * @param str     字符串日期
     * @param pattern 格式
     * @param zoneId  时区
     * @return 日期对象
     */
    public static LocalDate toLocalDate(String str, String pattern, ZoneId zoneId) {
        DateAssert.illegalArgument(str, "非法参数");
        DateAssert.illegalArgument(pattern, "非法参数");
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault();
        }
        return LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern)).atStartOfDay().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDate();
    }

    //-----------------------------------------------------------------------LocalTime---------------------------------------------------------------------

    /**
     * 将Date数据类型转换为LocalDate
     *
     * @param date 日期对象
     * @return 转换后的LocalDate对象
     */
    public static LocalTime toLocalTime(Date date) {
        DateAssert.illegalArgument(date, "非法参数");
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    /**
     * 将LocalDateTime转 LocalTime对象
     *
     * @param localDateTime 日期对象
     * @return LocalTime对象
     */
    public static LocalTime toLocalTime(LocalDateTime localDateTime) {
        DateAssert.illegalArgument(localDateTime, "非法参数");
        return localDateTime.toLocalTime();
    }

    /**
     * 将字符串日期转换为LocalDate对象
     *
     * @param str     字符串日期
     * @param pattern 格式
     * @return 日期对象
     */
    public static LocalTime toLocalTime(String str, String pattern) {
        DateAssert.illegalArgument(str, "非法参数");
        DateAssert.illegalArgument(pattern, "非法参数");
        return LocalTime.parse(str, DateTimeFormatter.ofPattern(pattern));
    }

    //----------------------------------------------------------------combine----------------------------------------------------------------------------

    /**
     * 将日期和时间类型拼接成 LocalDateTime对象
     *
     * @param date1 日期
     * @param date2 时间
     * @return 拼接后的时间对象
     */
    public static LocalDateTime combine(LocalDate date1, LocalTime date2) {
        DateAssert.illegalArgument(date1, "非法参数");
        DateAssert.illegalArgument(date2, "非法参数");
        return LocalDateTime.of(date1, date2);
    }

    /**
     * 将日期和时间类型拼接成 LocalDateTime对象
     *
     * @param date1    日期
     * @param pattern1 日期date1的格式类型
     * @param date2    时间
     * @param pattern2 日期date2的格式类型
     * @return 拼接后的时间对象
     */
    public static LocalDateTime combine(String date1, String pattern1, String date2, String pattern2) {
        DateAssert.illegalArgument(date1, "非法参数");
        DateAssert.illegalArgument(pattern1, "非法参数");
        DateAssert.illegalArgument(date2, "非法参数");
        DateAssert.illegalArgument(pattern2, "非法参数");
        return LocalDateTime.of(LocalDate.parse(date1, DateTimeFormatter.ofPattern(pattern1)), LocalTime.parse(date2, DateTimeFormatter.ofPattern(pattern2)));
    }


    //---------------------------------------------------------------dateToInt-----------------------------------------------------------------------------

    /**
     * 将日期对象转换为整数日期
     *
     * @param date    日期对象
     * @param pattern 日期 格式
     * @return 整数日期
     */
    public static long dateToInt(Date date, String pattern) {
        DateAssert.illegalArgument(date, "非法参数");
        DateAssert.illegalArgument(pattern, "非法参数");
        return Long.parseLong(format(date, pattern));
    }

    /**
     * 将时间对象转换为整数类型
     *
     * @param date    时间对象
     * @param pattern 日期格式
     * @return 整数类型日期
     */
    public static long dateToInt(LocalTime date, String pattern) {
        DateAssert.illegalArgument(date, "非法参数");
        pattern = DateAssert.requireElseGet(pattern, DatePatternInfo.HHMMSS);
        return Long.parseLong(date.format(DateTimeFormatter.ofPattern(pattern)));
    }

    /**
     * 将日期对象转换为整数类型
     *
     * @param date    日期对象
     * @param pattern 日期格式对象
     * @return 整数类型日期
     */
    public static long dateToInt(LocalDate date, String pattern) {
        DateAssert.illegalArgument(date, "非法参数");
        pattern = DateAssert.requireElseGet(pattern, DatePatternInfo.YYYYMMDD);
        return dateToInt(date.atStartOfDay(), pattern);
    }

    /**
     * 日期类型转换为整数类型
     *
     * @param date    日期对象
     * @param pattern 日期格式
     * @return 整数类型日期
     */
    public static long dateToInt(LocalDateTime date, String pattern) {
        DateAssert.illegalArgument(date, "非法参数");
        pattern = DateAssert.requireElseGet(pattern, DatePatternInfo.YYYYMMDDHHMMSS);
        return Long.parseLong(date.format(DateTimeFormatter.ofPattern(pattern)));
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------
}
