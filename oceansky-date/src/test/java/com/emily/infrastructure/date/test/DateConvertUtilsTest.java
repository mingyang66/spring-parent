package com.emily.infrastructure.date.test;

import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternType;
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
        Assert.assertEquals(DateConvertUtils.format("20230514", DatePatternType.YYYYMMDD.getPattern(), DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern()), "2023-05-14 00:00:00");

        Date date = DateConvertUtils.toDate("2023-05-14 12:52:56", DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern());
        Assert.assertEquals(DateConvertUtils.format(date, DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern()), "2023-05-14 12:52:56");
    }

    @Test
    public void toDate() {
        Assert.assertNotNull(DateConvertUtils.toDate("2023-05-14 12:52:56", DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern()));
        Assert.assertNotNull(DateConvertUtils.toDate(LocalDate.now()));
        Assert.assertNotNull(DateConvertUtils.toDate(LocalDateTime.now()));

        Assert.assertNotNull(DateConvertUtils.toLocalDateTime("2023-05-14 12:52:56", DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern()));
        Assert.assertNotNull(DateConvertUtils.toLocalDateTime(new Date()));
        Assert.assertNotNull(DateConvertUtils.toLocalDateTime(LocalDate.now()));

        Assert.assertNotNull(DateConvertUtils.toLocalDate("2023-05-14", DatePatternType.YYYY_MM_DD.getPattern()));
        Assert.assertNotNull(DateConvertUtils.toLocalDate(new Date()));
        Assert.assertNotNull(DateConvertUtils.toLocalDate(LocalDateTime.now()));

        Assert.assertNotNull(DateConvertUtils.toLocalTime("12:52:56", DatePatternType.HH_MM_SS.getPattern()));
        Assert.assertNotNull(DateConvertUtils.toLocalTime(new Date()));
        Assert.assertNotNull(DateConvertUtils.toLocalTime(LocalDateTime.now()));

        Assert.assertEquals(DateConvertUtils.toLocalDateTime(LocalDate.of(2023, 3, 14), LocalTime.of(12, 12, 12)).format(DateTimeFormatter.ofPattern(DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern())), "2023-03-14 12:12:12");
    }

}
