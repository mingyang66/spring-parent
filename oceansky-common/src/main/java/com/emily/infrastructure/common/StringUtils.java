package com.emily.infrastructure.common;

import java.util.Arrays;

/**
 * @Description :  字符串操作工具类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/4 2:13 PM
 */
public class StringUtils {
    /**
     * 空格字符串
     */
    public static final String SPACE = " ";

    /**
     * 空字符串
     */
    public static final String EMPTY = "";

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

    /**
     * 判断字符串是否为空
     * ----------------------------------------------------------
     * 示例：
     * Assert.assertTrue(StrUtils.isEmpty(null));
     * Assert.assertTrue(StrUtils.isEmpty(""));
     * Assert.assertFalse(StrUtils.isEmpty("a"));
     * Assert.assertFalse(StrUtils.isEmpty(" a"));
     * Assert.assertFalse(StrUtils.isEmpty(" a "));
     * ----------------------------------------------------------
     *
     * @param cs 字符串
     * @return true-为空 false-不为空
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     * -------------------------------------------------
     * 示例
     * Assert.assertFalse(StrUtils.isNotEmpty(null));
     * Assert.assertFalse(StrUtils.isNotEmpty(""));
     * Assert.assertTrue(StrUtils.isNotEmpty("a"));
     * Assert.assertTrue(StrUtils.isNotEmpty(" a"));
     * Assert.assertTrue(StrUtils.isNotEmpty(" a "));
     * -------------------------------------------------
     *
     * @param cs 字符串
     * @return true-不为空 false-为空
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判定字符串是否是空白
     * ---------------------------------------------------------
     * Assert.assertEquals(StringUtils.isBlank(null), true);
     * Assert.assertEquals(StringUtils.isBlank(""), true);
     * Assert.assertEquals(StringUtils.isBlank(" "), true);
     * Assert.assertEquals(StringUtils.isBlank(" a"), false);
     * Assert.assertEquals(StringUtils.isBlank(" a "), false);
     * ---------------------------------------------------------
     *
     * @param cs 字符串
     * @return true-空白 false-不是空白
     */
    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判定字符串是是非空白
     * -----------------------------------------------------
     * Assert.assertEquals(StringUtils.isNotBlank(null), false);
     * Assert.assertEquals(StringUtils.isNotBlank(""), false);
     * Assert.assertEquals(StringUtils.isNotBlank(" "), false);
     * Assert.assertEquals(StringUtils.isNotBlank(" a"), true);
     * Assert.assertEquals(StringUtils.isNotBlank(" a "), true);
     * -----------------------------------------------------
     *
     * @param cs 字符串
     * @return true-非空白 false-空白
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 字符串左侧拼接指定的字符达到指定的长度
     * ------------------------------------------------------------
     * 实例如下：
     * Assert.assertEquals(StrUtils.leftPad(null, 5, 'a'), null);
     * Assert.assertEquals(StrUtils.leftPad("", 5, '0'), "00000");
     * Assert.assertEquals(StrUtils.leftPad("aaaaa", 5, '0'), "aaaaa");
     * Assert.assertEquals(StrUtils.leftPad("2", 5, '0'), "00002");
     * ------------------------------------------------------------
     *
     * @param str     字符串
     * @param size    字符串总长度
     * @param padChar 要拼接的字符
     * @return 拼接后的字符串
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * 字符串左侧拼接上指定的字符串
     * --------------------------------------------------------
     * 示例如下：
     * Assert.assertEquals(StrUtils.leftPad(null, 5, "ab"), null);
     * Assert.assertEquals(StrUtils.leftPad(null, 5, null), null);
     * Assert.assertEquals(StrUtils.leftPad(null, 5, ""), null);
     * Assert.assertEquals(StrUtils.leftPad("", 5, ""), "     ");
     * Assert.assertEquals(StrUtils.leftPad("1", 5, ""), "    1");
     * Assert.assertEquals(StrUtils.leftPad("1", 5, " "), "    1");
     * Assert.assertEquals(StrUtils.leftPad("1", 5, "0"), "00001");
     * Assert.assertEquals(StrUtils.leftPad("1", 5, "0A"), "0A0A1");
     * Assert.assertEquals(StrUtils.leftPad("1", 5, "0AB"), "0AB01");
     * Assert.assertEquals(StrUtils.leftPad("11", 8, "0AB"), "0AB0AB11");
     * Assert.assertEquals(StrUtils.leftPad("11", 7, "0AB"), "0AB0A11");
     * --------------------------------------------------------
     *
     * @param str    字符串
     * @param size   拼接后字符串长度
     * @param padStr 要拼接的字符串
     * @return 拼接后的字符串
     */
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
     * 字符串右侧拼接上指定的字符达到指定的长度
     * -----------------------------------------------------
     * Assert.assertEquals(StringUtils.rightPad(null, 5, 'a'), null);
     * Assert.assertEquals(StringUtils.rightPad("", 5, '0'), "00000");
     * Assert.assertEquals(StringUtils.rightPad("aaaaa", 5, '0'), "aaaaa");
     * Assert.assertEquals(StringUtils.rightPad("2", 5, '0'), "20000");
     * -----------------------------------------------------
     *
     * @param str     字符串
     * @param size    拼接后字符串的总长度
     * @param padChar 要拼接的字符
     * @return 拼接后的字符串
     */
    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        return str.concat(repeat(padChar, pads));
    }

    /**
     * 将字符串右侧拼接上指定的字符串达到指定的长度
     * -----------------------------------------------------
     * Assert.assertEquals(StringUtils.rightPad(null, 5, "ab"), null);
     * Assert.assertEquals(StringUtils.rightPad(null, 5, null), null);
     * Assert.assertEquals(StringUtils.rightPad(null, 5, ""), null);
     * Assert.assertEquals(StringUtils.rightPad("", 5, ""), "     ");
     * Assert.assertEquals(StringUtils.rightPad("1", 5, ""), "1    ");
     * Assert.assertEquals(StringUtils.rightPad("1", 5, " "), "1    ");
     * Assert.assertEquals(StringUtils.rightPad("1", 5, "0"), "10000");
     * Assert.assertEquals(StringUtils.rightPad("1", 5, "0A"), "10A0A");
     * Assert.assertEquals(StringUtils.rightPad("1", 5, "0AB"), "10AB0");
     * Assert.assertEquals(StringUtils.rightPad("11", 8, "0AB"), "110AB0AB");
     * Assert.assertEquals(StringUtils.rightPad("11", 7, "0AB"), "110AB0A");
     * -----------------------------------------------------
     *
     * @param str    字符串
     * @param size   拼接后的长度
     * @param padStr 拼接字符串
     * @return 拼接后的字符串
     */
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * 将字符重复拼接N个拼接为字符串返回
     *
     * @param ch     字符
     * @param repeat 重复个数
     * @return 拼接后的字符串
     */
    public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return EMPTY;
        }
        final char[] buf = new char[repeat];
        Arrays.fill(buf, ch);
        return new String(buf);
    }

    /**
     * 获取字符串的长度
     * ----------------------------------------------------------
     * Assert.assertEquals(StrUtils.length(null), 0);
     * Assert.assertEquals(StrUtils.length(""), 0);
     * Assert.assertEquals(StrUtils.length("12"), 2);
     * ----------------------------------------------------------
     *
     * @param cs 字符串，如果为null，则长度为0
     * @return 字符串长度
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * 判定指定的字符串是否是数字
     * -----------------------------------------------
     * Assert.assertFalse(StringUtils.isNumeric(null));
     * Assert.assertFalse(StringUtils.isNumeric(""));
     * Assert.assertFalse(StringUtils.isNumeric(" "));
     * Assert.assertTrue(StringUtils.isNumeric("1"));
     * Assert.assertTrue(StringUtils.isNumeric("\u0967\u0968\u0969"));
     * Assert.assertFalse(StringUtils.isNumeric("1-"));
     * Assert.assertFalse(StringUtils.isNumeric("+1"));
     * Assert.assertFalse(StringUtils.isNumeric("-1"));
     * -----------------------------------------------
     *
     * @param cs 字符串
     * @return true-数字 false-否
     */
    public static boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int len = cs.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串使用指定的标识缩略
     * --------------------------------------------------------
     * 示例：
     * Assert.assertEquals(StringUtils.abbreviate(null, "...", 8), null);
     * Assert.assertEquals(StringUtils.abbreviate("", "...", 8), "");
     * Assert.assertEquals(StringUtils.abbreviate("a", "...", 8), "a...");
     * Assert.assertEquals(StringUtils.abbreviate("abc", "...", 8), "abc...");
     * Assert.assertEquals(StringUtils.abbreviate("abcde", "...", 8), "abcde...");
     * Assert.assertEquals(StringUtils.abbreviate("ABCDEFGHIJKLMN", "...", 8), "ABCDE...");
     * --------------------------------------------------------
     *
     * @param str          字符串
     * @param abbrevMarker 缩略标识，如：...
     * @param maxLength    缩略后字符串最大长度
     * @return
     */
    public static String abbreviate(final String str, final String abbrevMarker, int maxLength) {
        if (isEmpty(str)) {
            return str;
        }
        int strLen = str.length();
        int markerLen = abbrevMarker.length();
        if (strLen + markerLen <= maxLength) {
            return str.concat(abbrevMarker);
        }
        return str.substring(0, maxLength - markerLen).concat(abbrevMarker);
    }
}
