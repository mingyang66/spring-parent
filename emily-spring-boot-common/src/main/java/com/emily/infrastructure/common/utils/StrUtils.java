package com.emily.infrastructure.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * @program: spring-parent
 * @description: 字符串帮助类
 * @author: Emily
 * @create: 2021/08/19
 */
public class StrUtils {
    /**
     * 首字母转小写
     *
     * @param str
     * @return
     */
    public static String toLowerFirstCase(String str) {
        if (StringUtils.isEmpty(str) || Character.isLowerCase(str.charAt(0))) {
            return str;
        }
        return MessageFormat.format("{0}{1}", Character.toLowerCase(str.charAt(0)), str.substring(1));
    }

    /**
     * 首字母转大写
     *
     * @param str
     * @return
     */
    public static String toUpperFirstCase(String str) {
        if (StringUtils.isEmpty(str) || Character.isUpperCase(str.charAt(0))) {
            return str;
        }
        return MessageFormat.format("{0}{1}", Character.toUpperCase(str.charAt(0)), str.substring(1));
    }

    /**
     * 获取首个字符串
     *
     * @param str
     * @return
     */
    public static String firstString(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return String.valueOf(str.charAt(0));
    }

    /**
     * 截取字符串前缀指定字符数，如果小于等于指定长度，则直接返回，如果大于，则截取指定长度字符
     *
     * @param str    字符串
     * @param length 截取字符串长度
     * @return
     */
    public static String subFirstString(String str, int length) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (str.length() <= length) {
            return str;
        }
        return StringUtils.substring(str, 0, length);
    }

    /**
     * 截取字符串后半部分指定长度，如果小于等于长度，则直接返回，如果大于，则截取字符串后面指定长度字符
     *
     * @param str    字符串
     * @param length 截取字符串长度
     * @return
     */
    public static String subLastString(String str, int length) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (str.length() <= length) {
            return str;
        }
        return StringUtils.substring(str, str.length() - length);
    }

    /**
     * 获取字符串指定分隔符第一部分
     *
     * @param str       字符串
     * @param separator 分隔符
     * @return
     */
    public static String substringBeforeFirst(String str, String separator) {
        if (StringUtils.isNotEmpty(str) && StringUtils.isNotEmpty(separator)) {
            return str.split(separator)[0];
        }
        return str;
    }

}
