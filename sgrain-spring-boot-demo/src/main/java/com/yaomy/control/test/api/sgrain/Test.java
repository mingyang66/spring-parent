package com.yaomy.control.test.api.sgrain;

import com.google.common.collect.Lists;
import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.enums.DateFormatEnum;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.*;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/03/06
 */
public class Test {
    public static void main(String[] args) throws ParseException {

        System.out.println(compareTo(null, "2020-12-23 06:02:00", DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        System.out.println(compareTo("2020-12-23 08:02:00", "2020-12-23 07:02:00", DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        System.out.println(compareTo("2020-12-23 06:02:00", "2020-12-23 07:02:00", DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
    }

    /**
     * 比较日期大小，firstDate大于等于secondDate 返回true,否则返回false
     * @param firstDate 日期字符串
     * @param secondDate 日期字符串
     * @param format 日期格式
     * @return
     */
    public static boolean compareTo(String firstDate, String secondDate, String format) {
        try {
            Date first = DateUtils.parseDate(firstDate, format);
            Date second = DateUtils.parseDate(secondDate, format);
            if(first.compareTo(second) >= 0){
                return true;
            }
            return false;
        } catch (ParseException e){
            throw new BusinessException(AppHttpStatus.DATE_PARSE_EXCEPTION.getStatus(), AppHttpStatus.DATE_PARSE_EXCEPTION.getMessage());
        }
    }

    /**
     * 比较日期大小，firstDate大于等于secondDate 返回true,否则返回false
     * @param firstDate 日期字符串
     * @param secondDate 日期字符串
     * @return
     */
    public static boolean compareTo(Date firstDate, Date secondDate){
        if(firstDate.compareTo(secondDate) >= 0){
            return true;
        }
        return false;
    }
}
