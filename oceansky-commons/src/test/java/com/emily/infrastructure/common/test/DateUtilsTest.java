package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.date.DatePatternType;
import com.emily.infrastructure.common.date.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Description :  日期单元测试
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 3:27 PM
 */
public class DateUtilsTest {

    @Test
    public void asDate() {
        Assert.assertNotNull(DateUtils.toDate(LocalDateTime.now()));
        Assert.assertNotNull(DateUtils.toDate(LocalDate.now()));
    }

    @Test
    public void asLocalDateXX() {
        Assert.assertNotNull(DateUtils.toLocalDateTime(new Date()));
        Assert.assertNotNull(DateUtils.toLocalDate(new Date()));
        Assert.assertNotNull(DateUtils.toLocalTime(new Date()));
    }

    @Test
    public void asLocalDateXXX() {
        LocalDate localDate = LocalDate.parse("2023-05-14 00:00:00", DateTimeFormatter.ofPattern(DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern()));
        LocalDateTime localDateTime = DateUtils.toLocalDateTime(localDate);
        String str = localDateTime.format(DateTimeFormatter.ofPattern(DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern()));
        Assert.assertEquals("2023-05-14 00:00:00", str);
    }

    @Test
    public void parseLocalDateTime() {
        LocalDateTime localDateTime = DateUtils.parseLocalDateTime("2023-05-14 00:00:00", DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern());
        String str = localDateTime.format(DateTimeFormatter.ofPattern(DatePatternType.YYYY_MM_DD_HH_MM_SS.getPattern()));
        Assert.assertEquals("2023-05-14 00:00:00", str);
    }

    @Test
    public void parseLocalDate() {
        LocalDate localDate = DateUtils.parseLocalDate("2023-05-14", DatePatternType.YYYY_MM_DD.getPattern());
        String str = localDate.format(DateTimeFormatter.ofPattern(DatePatternType.YYYY_MM_DD.getPattern()));
        Assert.assertEquals("2023-05-14", str);
    }

    @Test
    public void parseLocalTime() {
        LocalTime localTime = DateUtils.parseLocalTime("12:56:58", DatePatternType.HH_MM_SS.getPattern());
        String str = localTime.format(DateTimeFormatter.ofPattern(DatePatternType.HH_MM_SS.getPattern()));
        Assert.assertEquals("12:56:58", str);
    }
}
