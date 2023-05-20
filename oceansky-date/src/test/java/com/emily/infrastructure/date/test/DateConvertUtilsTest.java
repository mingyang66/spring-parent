package com.emily.infrastructure.date.test;

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

}
