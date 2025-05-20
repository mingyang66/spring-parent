#### 解锁新技能《Java日期转换比较计算SDK》

开源pom依赖引用：

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-date</artifactId>
    <version>4.3.2</version>
</dependency>

```

##### 一、日期相互转换工具类DateConvertUtils

```java
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
    public static Date toDate(String str, String pattern) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str, pattern);
        } catch (ParseException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 将LocalDateTime转为Date
     *
     * @param localDateTime 日期类型
     * @return Date日期对象
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将LocalDateTime转为Date
     *
     * @param localDate 日期类型
     * @return Date日期对象
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将LocalDate转换为LocalDateTime
     *
     * @param localDate 日期对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        if (localDate == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return localDate.atStartOfDay();
    }

    /**
     * 将Date数据类型转换为LocalDateTime
     *
     * @param date 日期对象
     * @return 转换后的LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将字符串日期转换为LocalDateTime对象
     *
     * @param str     字符串日期
     * @param pattern 格式
     * @return 日期对象
     */
    public static LocalDateTime toLocalDateTime(String str, String pattern) {
        if (str == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将日期对象和时间对象拼接成一个日期对象
     *
     * @param date1 日期对象
     * @param date2 时间对象
     * @return 拼接后的时间对象
     */
    public static LocalDateTime toLocalDateTime(LocalDate date1, LocalTime date2) {
        if (date1 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (date2 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return LocalDateTime.of(date1, date2);
    }

    /**
     * 将指定时间转化为对应时区的时间
     * ------------------------------------------------------
     * zoneId示例：
     * ZoneId ZONE_CN = ZoneId.of("Asia/Shanghai");
     * ZoneId ZONE_US = ZoneId.of("US/Eastern")
     * ------------------------------------------------------
     *
     * @param date1  日期对象
     * @param zoneId 时区对象
     * @return 转换后的日期对象
     */
    public static LocalDateTime toLocalDateTime(LocalDateTime date1, ZoneId zoneId) {
        if (date1 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault();
        }
        return date1.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId).toLocalDateTime();
    }

    /**
     * 将Date数据类型转换为LocalDate
     *
     * @param date 日期对象
     * @return 转换后的LocalDate对象
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将LocalDateTime 转 LocalDate
     *
     * @param localDateTime 日期对象
     * @return LocalDate日期对象
     */
    public static LocalDate toLocalDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return localDateTime.toLocalDate();
    }

    /**
     * 将字符串日期转换为LocalDate对象
     *
     * @param str     字符串日期
     * @param pattern 格式
     * @return 日期对象
     */
    public static LocalDate toLocalDate(String str, String pattern) {
        if (str == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将Date数据类型转换为LocalDate
     *
     * @param date 日期对象
     * @return 转换后的LocalDate对象
     */
    public static LocalTime toLocalTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    /**
     * 将LocalDateTime转 LocalTime对象
     *
     * @param localDateTime 日期对象
     * @return LocalTime对象
     */
    public static LocalTime toLocalTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("非法参数");
        }
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
        if (str == null) {
            throw new IllegalArgumentException("非法参数");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return LocalTime.parse(str, DateTimeFormatter.ofPattern(pattern));
    }
}

```

##### 二、日期大小比较工具类DateCompareUtils

```java
public class DateCompareUtils {
    /**
     * 比较日期大小，firstDate大于等于secondDate 返回true,否则返回false
     *
     * @param date1   日期字符串
     * @param date2   日期字符串
     * @param pattern 日期格式
     * @return 1:date1>date2、0:date1=date2 -1:date1<date2
     */
    public static int compareTo(String date1, String date2, String pattern) {
        if (StringUtils.isEmpty(date1) || StringUtils.isEmpty(date2) || StringUtils.isEmpty(pattern)) {
            throw new IllegalArgumentException("非法参数");
        }
        try {
            Date first = org.apache.commons.lang3.time.DateUtils.parseDate(date1, pattern);
            Date second = org.apache.commons.lang3.time.DateUtils.parseDate(date2, pattern);
            return first.compareTo(second);
        } catch (ParseException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 比较日期大小，firstDate大于等于secondDate 返回true,否则返回false
     *
     * @param date1 日期字符串
     * @param date2 日期字符串
     * @return 1:date1>date2、0:date1=date2 -1:date1<date2
     */
    public static int compareTo(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return date1.compareTo(date2);
    }

    /**
     * 日期大小比较
     *
     * @param date1 日期对象
     * @param date2 日期对象
     * @return 1:date1>date2、0:date1=date2 -1:date1<date2
     */
    public static int compareTo(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return date1.compareTo(date2);
    }

    /**
     * 日期大小比较
     *
     * @param date1 日期对象
     * @param date2 日期对象
     * @return 1:date1>date2、0:date1=date2 -1:date1<date2
     */
    public static int compareTo(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return date1.compareTo(date2);
    }

    /**
     * 日期大小比较
     *
     * @param date1 日期对象
     * @param date2 日期对象
     * @return 1:date1>date2、0:date1=date2 -1:date1<date2
     */
    public static int compareTo(LocalTime date1, LocalTime date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return date1.compareTo(date2);
    }

    /**
     * Duration对象类型比较大小
     *
     * @param duration1 日期对象
     * @param duration2 日期对象
     * @return 1:duration1>duration2，0:duration1=duration2，-1:duration1<duration2
     */
    public static int compareTo(Duration duration1, Duration duration2) {
        if (duration1 == null || duration2 == null) {
            throw new IllegalArgumentException("非法参数");
        }
        return duration1.compareTo(duration2);
    }
}

```

##### 三、日期计算相关工具类DateComputeUtils

```java
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

```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)