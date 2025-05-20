package com.emily.infrastructure.date.test;

import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期计算单元测试类
 *
 * @author Emily
 * @since Created in 2023/5/14 3:13 PM
 */
public class DateComputeUtilsTest {

    @Test
    public void between() {
        Duration duration = DateComputeUtils.between(LocalDateTime.now().plusDays(5), LocalDateTime.now());
        Assertions.assertEquals(duration.getSeconds(), 431999);

        Duration duration1 = Duration.parse("P1DT1H10M10.5S");
        Assertions.assertEquals(duration1.toDays(), 1);
        Assertions.assertEquals(duration1.toHours(), 25);
        Assertions.assertEquals(duration1.toMinutes(), 1510);

    }

    @Test
    public void period() {
        Period period = DateComputeUtils.between(LocalDate.now().plusYears(2).plusDays(2), LocalDate.now());
        Assertions.assertEquals(period.getYears(), 2);
        Assertions.assertEquals(period.getMonths(), 0);
        Assertions.assertEquals(period.getDays(), 2);

        Period period1 = Period.parse("P12Y2M4D");
        Assertions.assertEquals(period1.getYears(), 12);
        Assertions.assertEquals(period1.getMonths(), 2);
        Assertions.assertEquals(period1.getDays(), 4);
    }

    @Test
    public void firstDayOfMonth() {
        LocalDate localDate1 = DateComputeUtils.firstDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45));
        Assertions.assertEquals(localDate1.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230301");

        LocalDate localDate2 = DateComputeUtils.firstDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), 2);
        Assertions.assertEquals(localDate2.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230501");

        LocalDate localDate3 = DateComputeUtils.firstDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), 0);
        Assertions.assertEquals(localDate3.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230301");

        LocalDate localDate4 = DateComputeUtils.firstDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), -1);
        Assertions.assertEquals(localDate4.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230201");
    }

    @Test
    public void lastDayOfMonth() {
        LocalDate localDate1 = DateComputeUtils.lastDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45));
        Assertions.assertEquals(localDate1.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230331");

        LocalDate localDate2 = DateComputeUtils.lastDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), 3);
        Assertions.assertEquals(localDate2.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230630");

        LocalDate localDate3 = DateComputeUtils.lastDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), 0);
        Assertions.assertEquals(localDate3.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20230331");

        LocalDate localDate4 = DateComputeUtils.lastDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45), -3);
        Assertions.assertEquals(localDate4.format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYYMMDD)), "20221231");

    }

    @Test
    public void getRemainDayOfMonth() {
        Assertions.assertEquals(DateComputeUtils.getRemainDayOfMonth(LocalDateTime.of(2023, 03, 26, 12, 23, 45)), 5);
        Assertions.assertEquals(DateComputeUtils.getRemainDayOfYear(LocalDateTime.of(2023, 03, 26, 12, 23, 45)), 280);
    }

    @Test
    public void duration() {
        Duration duration = DateComputeUtils.between(LocalDateTime.now(), LocalDateTime.now().minusDays(1).minusHours(10).minusSeconds(50));
        long hour = Duration.ofHours(48).minus(duration).toHours();
        Assertions.assertEquals(hour, 13);
    }

    @Test
    public void instant() {
        Instant instant3 = Instant.ofEpochSecond(1000);
        Instant instant4 = Instant.ofEpochSecond(2000);
        Assertions.assertEquals(DateComputeUtils.minusMillis(instant4, instant3), 1000000);
    }

    @Test
    public void shengyu() {
        long seconds = LocalDate.now().atTime(23, 59, 59).toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        System.out.println(seconds);
        Duration duration = DateComputeUtils.between(LocalDate.now().plusDays(1).atStartOfDay(), LocalDateTime.now());
        System.out.println(duration.getSeconds());
        System.out.println(DateComputeUtils.getRemainTimeOfDay().getSeconds());

        long second = Instant.ofEpochSecond(12).getEpochSecond();
        Assertions.assertEquals(second, 12);
        Assertions.assertEquals(Instant.ofEpochSecond(12).toEpochMilli(), 12000);
        Date data = Date.from(Instant.ofEpochSecond(LocalDateTime.now().getSecond()));
        System.out.println(DateConvertUtils.format(data, DatePatternInfo.YYYY_MM_DD_HH_MM_SS));
    }

    @Test
    public void isLeapYear() {
        Assertions.assertEquals(DateComputeUtils.isLeapYear(2004), true);
        Assertions.assertEquals(DateComputeUtils.isLeapYear(2000), true);
        Assertions.assertEquals(DateComputeUtils.isLeapYear(1900), false);
    }
}
