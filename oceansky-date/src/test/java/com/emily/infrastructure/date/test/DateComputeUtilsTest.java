package com.emily.infrastructure.date.test;

import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import org.junit.Assert;
import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @Description :  日期计算单元测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 3:13 PM
 */
public class DateComputeUtilsTest {

    @Test
    public void between() {
        Duration duration = DateComputeUtils.between(LocalDateTime.now().plusDays(5), LocalDateTime.now());
        Assert.assertEquals(duration.getSeconds(), 431999);

        Duration duration1 = Duration.parse("P1DT1H10M10.5S");
        Assert.assertEquals(duration1.toDays(), 1);
        Assert.assertEquals(duration1.toHours(), 25);
        Assert.assertEquals(duration1.toMinutes(), 1510);

    }

    @Test
    public void period() {
        Period period = DateComputeUtils.between(LocalDate.now().plusYears(2).plusDays(2), LocalDate.now());
        Assert.assertEquals(period.getYears(), 2);
        Assert.assertEquals(period.getMonths(), 0);
        Assert.assertEquals(period.getDays(), 2);

        Period period1 = Period.parse("P12Y2M4D");
        Assert.assertEquals(period1.getYears(), 12);
        Assert.assertEquals(period1.getMonths(), 2);
        Assert.assertEquals(period1.getDays(), 4);
    }

    @Test
    public void firstDayOfMonth() {
        LocalDate localDate1 = DateComputeUtils.firstDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45));
        Assert.assertEquals(localDate1.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230301");

        LocalDate localDate2 = DateComputeUtils.firstDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), 2);
        Assert.assertEquals(localDate2.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230501");

        LocalDate localDate3 = DateComputeUtils.firstDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), 0);
        Assert.assertEquals(localDate3.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230301");

        LocalDate localDate4 = DateComputeUtils.firstDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), -1);
        Assert.assertEquals(localDate4.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230201");
    }

    @Test
    public void lastDayOfMonth() {
        LocalDate localDate1 = DateComputeUtils.lastDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45));
        Assert.assertEquals(localDate1.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230331");

        LocalDate localDate2 = DateComputeUtils.lastDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), 3);
        Assert.assertEquals(localDate2.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230630");

        LocalDate localDate3 = DateComputeUtils.lastDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), 0);
        Assert.assertEquals(localDate3.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230331");

        LocalDate localDate4 = DateComputeUtils.lastDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), -3);
        Assert.assertEquals(localDate4.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20221231");

    }

    @Test
    public void getRemainDayOfMonth() {
        Assert.assertEquals(DateComputeUtils.getRemainDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45)), 5);
        Assert.assertEquals(DateComputeUtils.getRemainDayOfYear(LocalDateTime.of(2023, 03, 26, 12, 23, 45)), 280);
    }

    @Test
    public void duration() {
        Duration duration = DateComputeUtils.between(LocalDateTime.now(), LocalDateTime.now().minusDays(1).minusHours(10).minusSeconds(50));
        long hour = Duration.ofHours(48).minus(duration).toHours();
        Assert.assertEquals(hour, 13);
    }

    @Test
    public void zoneId() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 12, 12, 12, 12, 12);
        String s = DateConvertUtils.toLocalDateTime(localDateTime, ZoneId.of("US/Eastern")).format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYY_MM_DD_HH_MM_SS_EN));
        Assert.assertEquals(s, "2023/12/11 23:12:12");
    }
}
