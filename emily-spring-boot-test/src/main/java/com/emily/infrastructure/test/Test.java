package com.emily.infrastructure.test;

import com.emily.infrastructure.common.enums.DateFormatEnum;
import com.emily.infrastructure.common.utils.date.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(DateUtils.dateToNum(new Date(), DateFormatEnum.YYYYMMDD.getFormat()));
        System.out.println(DateUtils.dateToNum(LocalDate.now(), DateFormatEnum.YYYYMMDD.getFormat()));
        System.out.println(DateUtils.dateToNum(LocalDateTime.now(), DateFormatEnum.YYYYMMDD.getFormat()));
        System.out.println(DateUtils.numToDate(20210523L, DateFormatEnum.YYYYMMDD.getFormat()));
        System.out.println(DateUtils.numToLocalDate(20210523L, DateFormatEnum.YYYYMMDD.getFormat()));
        System.out.println(DateUtils.numToLocalDateTime(2021052312L, "yyyyMMddHH"));
        System.out.println(DateUtils.numToStr(202105231212L, DateFormatEnum.YYYYMMDDHH.getFormat(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat()));
        System.out.println(DateUtils.strToNum("2021-05-23", DateFormatEnum.YYYY_MM_DD.getFormat(), DateFormatEnum.YYYYMMDD.getFormat()));

    }
}
