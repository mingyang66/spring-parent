package com.emily.infrastructure.language.i18n;

/**
 * 简繁体多语言帮助类
 *
 * @author Emily
 */
public class I18nChineseHelper {
    public static final I18nDictionary S2T_Container = I18nResource.getDictionary(I18nType.SIMPLE_TO_TRADITIONAL);
    public static final I18nDictionary T2S_Container = I18nResource.getDictionary(I18nType.TRADITIONAL_TO_SIMPLE);
    /**
     * 汉字正则表达式
     */
    private static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";

    /**
     * 简体转繁体
     *
     * @return 繁体字符串
     */
    public static String convertToTraditionalChinese(String content) {
        return S2T_Container.convert(content);
    }


    /**
     * 繁体转简体
     *
     * @param content 简体字符串
     * @return 翻译后繁体字符串
     */
    public static String convertToSimplifiedChinese(String content) {
        return T2S_Container.convert(content);
    }

    /**
     * 判断某个字符是否为汉字
     *
     * @param c 需要判断的字符
     * @return 是汉字返回true，否则返回false
     */
    public static boolean isChinese(char c) {
        return '〇' == c || String.valueOf(c).matches(CHINESE_REGEX);
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 字符串
     * @return 包含汉字返回true，否则返回false
     */
    public static boolean containsChinese(String str) {
        for (int i = 0, len = str.length(); i < len; i++) {
            if (isChinese(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
