package com.emily.infrastructure.common;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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
        return String.valueOf(str.charAt(0)).toUpperCase().concat(str.substring(1));
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
        return String.valueOf(str.charAt(0)).toLowerCase().concat(str.substring(1));
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
     * 如果字符串为null或者空字符串，则返回默认字符串
     * -----------------------------------------------------
     * 示例：
     * Assert.assertEquals(StringUtils.defaultIfEmpty(null, "ab"),"ab");
     * Assert.assertEquals(StringUtils.defaultIfEmpty("", "ab"),"ab");
     * Assert.assertEquals(StringUtils.defaultIfEmpty(" ", "ab")," ");
     * Assert.assertEquals(StringUtils.defaultIfEmpty("1", "ab"),"1");
     * -----------------------------------------------------
     *
     * @param str        字符串
     * @param defaultStr 默认字符串
     * @param <T>        字符类型
     * @return 最终结果
     */
    public static <T extends CharSequence> T defaultIfEmpty(final T str, final T defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * 如果字符串为nul，则返回defaultStr字符串
     * -------------------------------------------------------
     * 示例：
     * Assert.assertEquals(StringUtils.defaultString(null, "ab"), "ab");
     * Assert.assertEquals(StringUtils.defaultString("", "ab"), "");
     * Assert.assertEquals(StringUtils.defaultString("1", "ab"), "1");
     * -------------------------------------------------------
     *
     * @param str        字符串
     * @param defaultStr 默认字符串
     * @param <T>        字符类型
     * @return 最终字符串结果
     */
    public static <T extends CharSequence> T defaultString(final T str, final T defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * 如果字符为null、""、" "，则返回默认字符串
     * ------------------------------------------------------
     * 示例：
     * Assert.assertEquals(StringUtils.defaultIfBlank(null, "ab"),"ab");
     * Assert.assertEquals(StringUtils.defaultIfBlank("", "ab"),"ab");
     * Assert.assertEquals(StringUtils.defaultIfBlank(" ", "ab"),"ab");
     * Assert.assertEquals(StringUtils.defaultIfBlank("1", "ab"),"1");
     * ------------------------------------------------------
     *
     * @param str        字符串
     * @param defaultStr 默认字符串
     * @param <T>        字符类型
     * @return 最终结果
     */
    public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultStr) {
        return isBlank(str) ? defaultStr : str;
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
     * @return 缩略后的字符串
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

    /**
     * 判定字符串是否以指定的前缀开头
     * -------------------------------------------------------------------------------
     * 示例：
     * Assert.assertFalse(StringUtils.startsWith(null, "ab", 0, false));
     * Assert.assertFalse(StringUtils.startsWith("", "ab", 0, false));
     * Assert.assertFalse(StringUtils.startsWith(null, null, 0, false));
     * Assert.assertFalse(StringUtils.startsWith("", "", 0, false));
     * Assert.assertTrue(StringUtils.startsWith("abcd", "ab", 0, false));
     * Assert.assertTrue(StringUtils.startsWith("abcd", "ab", 0, true));
     * Assert.assertFalse(StringUtils.startsWith("Abcd", "ab", 0, false));
     * Assert.assertTrue(StringUtils.startsWith("Abcd", "ab", 0, true));
     * Assert.assertFalse(StringUtils.startsWith("AbCd", "bc", 1, false));
     * Assert.assertTrue(StringUtils.startsWith("AbCd", "bc", 1, true));
     * Assert.assertFalse(StringUtils.startsWith("AbCd", "bc", 3, true));
     * Assert.assertFalse(StringUtils.startsWith("AbCd", "bc", 4, true));
     * -------------------------------------------------------------------------------
     *
     * @param str        字符串
     * @param prefix     前缀
     * @param toffset    起始索引
     * @param ignoreCase 是否忽略大小写
     * @return true-是，false-否
     */
    public static boolean startsWith(final String str, final String prefix, int toffset, final boolean ignoreCase) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return false;
        }
        if (ignoreCase) {
            return str.toLowerCase().startsWith(prefix.toLowerCase(), toffset);
        }
        return str.startsWith(prefix, toffset);
    }

    /**
     * 判定字符串是否以指定的前缀开头
     * ----------------------------------------------------------
     * 示例：
     * Assert.assertFalse(StringUtils.startsWith(null, null));
     * Assert.assertFalse(StringUtils.startsWith(null, ""));
     * Assert.assertFalse(StringUtils.startsWith(null, "a"));
     * Assert.assertFalse(StringUtils.startsWith("", null));
     * Assert.assertFalse(StringUtils.startsWith("", ""));
     * Assert.assertTrue(StringUtils.startsWith("abb", "ab"));
     * Assert.assertFalse(StringUtils.startsWith("abb", "Ab"));
     * ----------------------------------------------------------
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return true-是，false-否
     */
    public static boolean startsWith(final String str, final String prefix) {
        return startsWith(str, prefix, 0, false);
    }

    /**
     * 判定字符串是否以指定的前缀开头
     * ----------------------------------------------------------
     * 示例：
     * Assert.assertFalse(StringUtils.startsWithIgnoreCase(null, null));
     * Assert.assertFalse(StringUtils.startsWithIgnoreCase(null, ""));
     * Assert.assertFalse(StringUtils.startsWithIgnoreCase(null, "a"));
     * Assert.assertFalse(StringUtils.startsWithIgnoreCase("", null));
     * Assert.assertFalse(StringUtils.startsWithIgnoreCase("", ""));
     * Assert.assertTrue(StringUtils.startsWithIgnoreCase("abb", "ab"));
     * Assert.assertTrue(StringUtils.startsWithIgnoreCase("abb", "Ab"));
     * ----------------------------------------------------------
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return true-是，false-否
     */
    public static boolean startsWithIgnoreCase(final String str, final String prefix) {
        return startsWith(str, prefix, 0, true);
    }

    /**
     * 判定字符串是否以指定后缀结尾
     * -------------------------------------------------------
     * 示例：
     * Assert.assertFalse(StringUtils.endsWith(null, null, false));
     * Assert.assertFalse(StringUtils.endsWith(null, "", false));
     * Assert.assertFalse(StringUtils.endsWith("", null, false));
     * Assert.assertFalse(StringUtils.endsWith("", "", false));
     * Assert.assertTrue(StringUtils.endsWith("abcd", "cd", false));
     * Assert.assertTrue(StringUtils.endsWith("abcd", "cD", true));
     * -------------------------------------------------------
     *
     * @param str        字符串
     * @param suffix     前缀
     * @param ignoreCase 是否忽略大小写
     * @return true-是，false-否
     */
    public static boolean endsWith(final String str, final String suffix, final boolean ignoreCase) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return false;
        }
        if (ignoreCase) {
            return str.toLowerCase().endsWith(suffix.toLowerCase());
        }
        return str.endsWith(suffix);
    }

    /**
     * 判定字符串是否以指定后缀结尾
     * -------------------------------------------------------
     * 示例：
     * Assert.assertFalse(StringUtils.endsWith(null, null));
     * Assert.assertFalse(StringUtils.endsWith(null, ""));
     * Assert.assertFalse(StringUtils.endsWith("", null));
     * Assert.assertFalse(StringUtils.endsWith("", ""));
     * Assert.assertTrue(StringUtils.endsWith("abcd", "cd"));
     * Assert.assertFalse(StringUtils.endsWith("abcd", "cD"));
     * -------------------------------------------------------
     *
     * @param str    字符串
     * @param suffix 前缀
     * @return true-是，false-否
     */
    public static boolean endsWith(final String str, final String suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * 判定字符串是否以指定后缀结尾
     * -------------------------------------------------------
     * 示例：
     * Assert.assertFalse(StringUtils.endsWithIgnoreCase(null, null));
     * Assert.assertFalse(StringUtils.endsWithIgnoreCase(null, ""));
     * Assert.assertFalse(StringUtils.endsWithIgnoreCase("", null));
     * Assert.assertFalse(StringUtils.endsWithIgnoreCase("", ""));
     * Assert.assertTrue(StringUtils.endsWithIgnoreCase("abcd", "cd"));
     * Assert.assertTrue(StringUtils.endsWithIgnoreCase("abcd", "cD"));
     * -------------------------------------------------------
     *
     * @param str    字符串
     * @param suffix 前缀
     * @return true-是，false-否
     */
    public static boolean endsWithIgnoreCase(final String str, final String suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * 获取字符串对应的字节码数组
     * ----------------------------------------------------------------
     * 示例：
     * Assert.assertEquals(StringUtils.getBytes(null, null).length, 0);
     * Assert.assertEquals(StringUtils.getBytes("", null).length, 0);
     * Assert.assertEquals(StringUtils.getBytes("ab", Charset.defaultCharset()).length, 2);
     * ----------------------------------------------------------------
     *
     * @param string  字符串
     * @param charset 编码类型
     * @return 字节数组
     */
    public static byte[] getBytes(final String string, final Charset charset) {
        return string == null ? ArrayUtils.EMPTY_BYTE_ARRAY : string.getBytes(Charsets.toCharset(charset));
    }

    /**
     * 将字符串转换为字节数组
     *
     * @param string  字符串
     * @param charset 编码名称
     */
    public static byte[] getBytes(final String string, final String charset) throws UnsupportedEncodingException {
        return string == null ? ArrayUtils.EMPTY_BYTE_ARRAY : string.getBytes(Charsets.toCharsetName(charset));
    }

    /**
     * 字符串中的字符序列替换
     * ---------------------------------------------------------
     * 示例：
     * Assert.assertEquals(StringUtils.replace("abc", "ab", "AB"), "ABc");
     * Assert.assertThrows(IllegalArgumentException.class, () -> StringUtils.replace("abc", "", ""));
     * Assert.assertThrows(IllegalArgumentException.class, () -> StringUtils.replace("abc", null, ""));
     * ---------------------------------------------------------
     *
     * @param str         字符串
     * @param target      要替换的字符值序列
     * @param replacement 字符值的替换序列
     * @return 替换后的字符串
     */
    public static String replace(final String str, final CharSequence target, final CharSequence replacement) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(target) || isEmpty(replacement)) {
            throw new IllegalArgumentException("非法参数");
        }
        return str.replace(target, replacement);
    }

    /**
     * 替换字符串中的指定字符
     * -------------------------------------------------------------
     * 示例：
     * Assert.assertEquals(StringUtils.replace("abc", 'b', 'B'), "aBc");
     * -------------------------------------------------------------
     *
     * @param str     字符串
     * @param oldChar 要被替换的字符
     * @param newChar 字符的替换字符
     * @return 替换字符后的字符串
     */
    public static String replace(final String str, final char oldChar, final char newChar) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replace(oldChar, newChar);
    }
}