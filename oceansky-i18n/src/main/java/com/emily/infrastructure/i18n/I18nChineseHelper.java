package com.emily.infrastructure.i18n;

/**
 * 简繁体多语言帮助类
 *
 * @author Emily
 */
public class I18nChineseHelper {
    public static final I18nDictionary s2tContainer = I18nResource.getDictionary(I18nType.SIMPLE_TO_TRADITIONAL);
    public static final I18nDictionary t2sContainer = I18nResource.getDictionary(I18nType.TRADITIONAL_TO_SIMPLE);


    /**
     * 简体转繁体
     *
     * @return
     */
    public static String convertToTraditionalChinese(String content) {
        return s2tContainer.convert(content);
    }


    /**
     * 繁体转简体
     *
     * @param content
     * @return
     */
    public static String convertToSimplifiedChinese(String content) {
        return t2sContainer.convert(content);
    }
}
