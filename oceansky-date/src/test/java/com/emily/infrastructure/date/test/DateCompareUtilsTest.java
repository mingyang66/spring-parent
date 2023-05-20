package com.emily.infrastructure.date.test;

import com.emily.infrastructure.date.DateCompareUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @Description :  日期大小比较
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 5:23 PM
 */
public class DateCompareUtilsTest {
    @Test
    public void compareTest() {
        Date date1 = DateConvertUtils.toDate("2023-05-14 12:56:28", DatePatternInfo.YYYY_MM_DD_HH_MM_SS);
        Date date2 = DateConvertUtils.toDate("2023-05-14 12:56:26", DatePatternInfo.YYYY_MM_DD_HH_MM_SS);
        Assert.assertEquals(DateCompareUtils.compareTo(date1, date2), 1);

        Assert.assertEquals(DateCompareUtils.compareTo("2023-05-14 12:56:28", "2023-05-14 12:56:29", DatePatternInfo.YYYY_MM_DD_HH_MM_SS), -1);
    }

    @Test
    public void compareLocalDateTime() {
        LocalDateTime date1 = DateConvertUtils.toLocalDateTime("2023-05-14 12:56:28", DatePatternInfo.YYYY_MM_DD_HH_MM_SS);
        LocalDateTime date2 = DateConvertUtils.toLocalDateTime("2023-05-14 12:56:29", DatePatternInfo.YYYY_MM_DD_HH_MM_SS);

        Assert.assertEquals(DateCompareUtils.compareTo(date1, date2), -1);

    }

    @Test
    public void compareLocalDate() {
        LocalDate date1 = DateConvertUtils.toLocalDate("2023-05-14", DatePatternInfo.YYYY_MM_DD);
        LocalDate date2 = DateConvertUtils.toLocalDate("2023-05-14", DatePatternInfo.YYYY_MM_DD);

        Assert.assertEquals(DateCompareUtils.compareTo(date1, date2), 0);

    }

    @Test
    public void compareLocalTime() {
        LocalTime date1 = DateConvertUtils.toLocalTime("12:56:28", DatePatternInfo.HH_MM_SS);
        LocalTime date2 = DateConvertUtils.toLocalTime("12:56:29", DatePatternInfo.HH_MM_SS);

        Assert.assertEquals(DateCompareUtils.compareTo(date1, date2), -1);

    }

    @Test
    public void compareDuration() {
        Assert.assertEquals(DateCompareUtils.compareTo(Duration.ofDays(10), Duration.ofDays(9)), 1);
        Assert.assertEquals(DateCompareUtils.compareTo(Duration.ofDays(10), Duration.ofDays(10)), 0);
        Assert.assertEquals(DateCompareUtils.compareTo(Duration.ofDays(10), Duration.ofDays(11)), -1);
    }
}
