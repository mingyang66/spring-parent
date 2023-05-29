package com.emily.infrastructure.date.test;

import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Description :  单元测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 3:24 PM
 */
public class DateConvertUtilsTest {
    @Test
    public void format() {
        Assert.assertEquals(DateConvertUtils.format("20230514", DatePatternInfo.YYYYMMDD, DatePatternInfo.YYYY_MM_DD_HH_MM_SS), "2023-05-14 00:00:00");

        Date date = DateConvertUtils.toDate("2023-05-14 12:52:56", DatePatternInfo.YYYY_MM_DD_HH_MM_SS);
        Assert.assertEquals(DateConvertUtils.format(date, DatePatternInfo.YYYY_MM_DD_HH_MM_SS), "2023-05-14 12:52:56");

        LocalDateTime localDateTime = LocalDateTime.of(2023, 06, 01, 8, 52, 53);
        Assert.assertEquals(DateConvertUtils.format(localDateTime, DatePatternInfo.YYYY_MM_DD_HH_MM_SS), "2023-06-01 08:52:53");

        LocalDate localDate = LocalDate.of(2023, 06, 01);
        Assert.assertEquals(DateConvertUtils.format(localDate, DatePatternInfo.YYYY_MM_DD), "2023-06-01");

        LocalTime localTime = LocalTime.of(8, 52, 53);
        Assert.assertEquals(DateConvertUtils.format(localTime, DatePatternInfo.HH_MM_SS), "08:52:53");
    }

    @Test
    public void toDate() {
        Assert.assertNotNull(DateConvertUtils.toDate("2023-05-14 12:52:56", DatePatternInfo.YYYY_MM_DD_HH_MM_SS));
        Assert.assertNotNull(DateConvertUtils.toDate(LocalDate.now()));
        Assert.assertNotNull(DateConvertUtils.toDate(LocalDateTime.now()));

        Assert.assertNotNull(DateConvertUtils.toLocalDateTime("2023-05-14 12:52:56", DatePatternInfo.YYYY_MM_DD_HH_MM_SS));
        Assert.assertNotNull(DateConvertUtils.toLocalDateTime(new Date()));
        Assert.assertNotNull(DateConvertUtils.toLocalDateTime(LocalDate.now()));

        Assert.assertNotNull(DateConvertUtils.toLocalDate("2023-05-14", DatePatternInfo.YYYY_MM_DD));
        Assert.assertNotNull(DateConvertUtils.toLocalDate(new Date()));
        Assert.assertNotNull(DateConvertUtils.toLocalDate(LocalDateTime.now()));

        Assert.assertNotNull(DateConvertUtils.toLocalTime("12:52:56", DatePatternInfo.HH_MM_SS));
        Assert.assertNotNull(DateConvertUtils.toLocalTime(new Date()));
        Assert.assertNotNull(DateConvertUtils.toLocalTime(LocalDateTime.now()));

        Assert.assertEquals(DateConvertUtils.toLocalDateTime(LocalDate.of(2023, 3, 14), LocalTime.of(12, 12, 12)).format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYY_MM_DD_HH_MM_SS)), "2023-03-14 12:12:12");
    }

    @Test
    public void combine() {
        LocalDateTime localDateTime = DateConvertUtils.combine("20230506", DatePatternInfo.YYYYMMDD, "05:23:21", DatePatternInfo.HH_MM_SS);
        String s = LocalDateTime.of(localDateTime.toLocalDate(), localDateTime.toLocalTime()).format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYY_MM_DD_HH_MM_SS));
        Assert.assertEquals(s, "2023-05-06 05:23:21");
        Assert.assertNotNull(DateConvertUtils.combine(LocalDate.now(), LocalTime.now()));
    }

    @Test
    public void dateToInt() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 06, 01, 8, 52, 53);
        Assert.assertEquals(DateConvertUtils.dateToInt(localDateTime, DatePatternInfo.YYYYMMDDHHMMSS), 20230601085253L);
        Assert.assertEquals(DateConvertUtils.dateToInt(localDateTime.toLocalDate(), null), 20230601);
        Assert.assertEquals(DateConvertUtils.dateToInt(localDateTime.toLocalDate(), DatePatternInfo.YYYYMMDD), 20230601);
        Assert.assertEquals(DateConvertUtils.dateToInt(localDateTime.toLocalTime(), null), 85253);
        Assert.assertEquals(DateConvertUtils.dateToInt(localDateTime.toLocalTime(), DatePatternInfo.HHMMSS), 85253);
        Assert.assertEquals(DateConvertUtils.dateToInt(localDateTime.toLocalTime(), DatePatternInfo.HHMM), 852);

        Date date = DateConvertUtils.toDate("20230201", DatePatternInfo.YYYYMMDD);
        Assert.assertEquals(DateConvertUtils.dateToInt(date, DatePatternInfo.YYYYMMDD), 20230201);
    }
    @Test
    public void timestamp(){
        Date date = DateConvertUtils.toDate(1685353612112L);
        Assert.assertEquals(DateConvertUtils.format(date, DatePatternInfo.YYYY_MM_DD_HH_MM_SS),"2023-05-29 17:46:52");

        Date date1 = DateConvertUtils.toDate(0);
        Assert.assertEquals(DateConvertUtils.format(date1, DatePatternInfo.YYYY_MM_DD_HH_MM_SS),"1970-01-01 08:00:00");
    }
}
