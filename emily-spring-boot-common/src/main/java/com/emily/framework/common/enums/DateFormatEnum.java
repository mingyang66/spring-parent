package com.emily.framework.common.enums;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @Description: 日期格式
 * @Version: 1.0
 */
@SuppressWarnings("all")
public enum DateFormatEnum {
    YYYY_MM("yyyy-MM"),
    YYYY_MM_DD("yyyy-MM-dd"),
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    YYYY_MM_DD_HH_MM_SS_SSS("yyyy-MM-dd HH:mm:ss.SSS"),
    YYYY_MM_DDTHH_MM_SS_COLON_SSS("yyyy-MM-dd'T'HH:mm:ss:SSS"),

    YYYY("yyyy"),
    YYYYMM("yyyyMM"),
    YYYYMMDDHHMMSS("yyyyMMddHHmmss"),
    HHMMSS("HHmmss"),
    MMSS("mmss"),
    HHMM("HHmm"),
    SS("ss"),

    YYYY_MM_EN("yyyy/MM"),
    YYYY_MM_DD_EN("yyyy/MM/dd"),
    YYYY_MM_DD_HH_MM_EN("yyyy/MM/dd HH:mm"),
    YYYY_MM_DD_HH_MM_SS_EN("yyyy/MM/dd HH:mm:ss"),
    YY_MM_DD_EN("yy/MM/dd"),
    MM_DD_EN("MM/dd"),
    MM_DD_HH_MM_EN("MM/dd HH:mm"),
    MM_DD_HH_MM_SS_EN("MM/dd HH:mm:ss"),
    MM_DD_HHTMM_SS_SSSZ_EN("MM-dd’T’HH:mm:ss.SSSZ"),


    YYYY_MM_CN("yyyy年MM月"),
    YYYY_MM_DD_CN("yyyy年MM月dd日"),
    YYYY_MM_DD_HH_MM_CN("yyyy年MM月dd日 HH:mm"),
    YYYY_MM_DD_HH_MM_SS_CN("yyyy年MM月dd日 HH:mm:ss"),

    HH_MM("HH:mm"),
    HH_MM_SS("HH:mm:ss"),

    YYYY_MM_SPOT("yyyy.MM"),
    YYYY_MM_DD_SPOT("yyyy.MM.dd"),

    MM_DD("MM-dd"),
    MM_DD_HH_MM("MM-dd HH:mm"),
    MM_DD_HH_MM_SS("MM-dd HH:mm:ss"),

    MM_DD_CN("MM月dd日"),
    MM_DD_HH_MM_CN("MM月dd日 HH:mm"),
    MM_DD_HH_MM_SS_CN("MM月dd日 HH:mm:ss"),

    YYYY_MM_DD_AHH_MM_SS("yyyy-MM-dd aHH:mm:ss"),
    YYYY_MM_DD_AHH_MM_SS_SSS("yyyy-MM-dd aHH:mm:ss.SSS");


    private final String format;
    DateFormatEnum(String format){
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    /**
     * 获取所有日期格式
     * @return
     */
    public static String[] getAllFormats(){
        String[] formats = new String[]{};
        DateFormatEnum[] formatEnums = DateFormatEnum.values();
        for(int i=0; i<formatEnums.length; i++){
            formats = ArrayUtils.add(formats, formatEnums[i].getFormat());
        }
        return formats;
    }
}
