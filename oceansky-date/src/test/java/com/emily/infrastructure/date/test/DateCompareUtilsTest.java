package com.emily.infrastructure.date.test;

import com.emily.infrastructure.date.DateCompareUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import org.junit.Assert;
import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期大小比较
 *
 * @author Emily
 * @since Created in 2023/5/14 5:23 PM
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

    @Test
    public void instant() {
        String s1 = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYY_MM_DD_HHTMM_SS_SSSZ_EN));
        System.out.println(s1);
        Instant instant1 = Instant.parse("2023-05-20T15:33:58.093Z");
        Instant instant2 = Instant.parse("2023-05-20T15:34:58.093Z");
        Assert.assertEquals(DateCompareUtils.compareTo(instant1, instant2), -1);
        Assert.assertEquals(DateCompareUtils.compareTo(instant2, instant1), 1);
        //秒
        Assert.assertEquals(instant1.getEpochSecond(), 1684596838);
        //纳秒
        Assert.assertEquals(instant1.getNano(), 93000000);
        //毫秒
        Assert.assertEquals(instant1.toEpochMilli(), 1684596838093l);

    }

    @Test
    public void isBefore() {
        LocalDateTime date1 = DateConvertUtils.toLocalDateTime("2023-05-14 12:56:29", DatePatternInfo.YYYY_MM_DD_HH_MM_SS);
        LocalDateTime date2 = DateConvertUtils.toLocalDateTime("2023-05-14 12:56:28", DatePatternInfo.YYYY_MM_DD_HH_MM_SS);
        Assert.assertEquals(DateCompareUtils.isBefore(date1, date2), false);
        Assert.assertEquals(DateCompareUtils.isAfter(date1, date2), true);
        Assert.assertEquals(DateCompareUtils.isEqual(date1, date2), false);
    }
}
