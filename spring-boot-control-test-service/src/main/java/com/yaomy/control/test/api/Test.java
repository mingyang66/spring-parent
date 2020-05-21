package com.yaomy.control.test.api;

import com.sgrain.boot.common.enums.DateFormatEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/03/06
 */
public class Test {
    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        System.out.println(localDateTime.toString());
        System.out.println(localDateTime.format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat())));
        System.out.println(localDateTime.format(DateTimeFormatter.ISO_DATE));
        System.out.println(localDateTime.format(DateTimeFormatter.ISO_TIME));
        System.out.println(localDateTime.format(DateTimeFormatter.ISO_WEEK_DATE));
        System.out.println(localDateTime.format(DateTimeFormatter.ISO_ORDINAL_DATE));
        System.out.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME));

    }
}
