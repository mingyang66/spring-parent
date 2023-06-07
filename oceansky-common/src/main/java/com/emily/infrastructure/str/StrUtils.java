package com.emily.infrastructure.str;

/**
 * @Description :  字符串操作工具类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/4 2:13 PM
 */
public class StrUtils {
    /**
     * 首字母转大写
     * ----------------------------------------------
     * 示例程序：
     * Assert.assertEquals(StrUtils.toUpperFirstCase(null), null);
     * Assert.assertEquals(StrUtils.toUpperFirstCase(""), "");
     * Assert.assertEquals(StrUtils.toUpperFirstCase(" "), " ");
     * Assert.assertEquals(StrUtils.toUpperFirstCase(" a".trim()), "A");
     * Assert.assertEquals(StrUtils.toUpperFirstCase("a"), "A");
     * Assert.assertEquals(StrUtils.toUpperFirstCase("abc"), "Abc");
     * ----------------------------------------------
     *
     * @param str 字符串
     * @return 转换后的字符串
     */
    public static String toUpperFirstCase(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return String.join("", String.valueOf(str.charAt(0)).toUpperCase(), str.substring(1));
    }

    /**
     * 将字符串首字母转为小写
     * ---------------------------------------------
     * Assert.assertEquals(StrUtils.toLowerFirstCase(null), null);
     * Assert.assertEquals(StrUtils.toLowerFirstCase(""), "");
     * Assert.assertEquals(StrUtils.toLowerFirstCase(" "), " ");
     * Assert.assertEquals(StrUtils.toLowerFirstCase(" A"), " A");
     * Assert.assertEquals(StrUtils.toLowerFirstCase("A"), "a");
     * Assert.assertEquals(StrUtils.toLowerFirstCase("Abc"), "abc");
     * ---------------------------------------------
     *
     * @param str 字符串
     * @return 转换后的字符串
     */
    public static String toLowerFirstCase(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        return String.join("", String.valueOf(str.charAt(0)).toLowerCase(), str.substring(1));
    }
}
