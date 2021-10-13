package com.emily.infrastructure.core.helper;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * @program: spring-parent
 * @description: 字符串帮助类
 * @author: Emily
 * @create: 2021/08/19
 */
public class StringHelper {
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
     * @param str
     * @return
     */
    public static String firstString(String str){
        if(StringUtils.isEmpty(str)){
            return str;
        }
        return String.valueOf(str.charAt(0));
    }
}
