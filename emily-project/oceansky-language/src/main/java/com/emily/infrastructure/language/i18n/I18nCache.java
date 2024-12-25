package com.emily.infrastructure.language.i18n;

import com.emily.infrastructure.language.convert1.I18nChineseHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 异常多语言缓存
 *
 * @author Emily
 * @since Created in 2022/8/9 7:38 下午
 */
public class I18nCache {
    /**
     * 简体-繁体
     */
    private static final ConcurrentMap<String, String> zhMap = new ConcurrentHashMap<>();
    /**
     * 简体-英文
     */
    private static final ConcurrentMap<String, String> enMap = new ConcurrentHashMap<>();

    /**
     * 简体-繁体 绑定
     *
     * @param simple      简体
     * @param traditional 繁体
     */
    public static void bindZh(String simple, String traditional) {
        zhMap.put(simple, traditional);
    }

    /**
     * 简体-繁体 绑定
     *
     * @param zhCache 语言映射关系对象
     */
    public static void bindZh(Map<String, String> zhCache) {
        zhMap.putAll(zhCache);
    }

    /**
     * 简体-英文 绑定
     *
     * @param simple 简体
     * @param en     英文
     */
    public static void bindEn(String simple, String en) {
        enMap.put(simple, en);
    }

    /**
     * 简体-英文 绑定
     *
     * @param enCache 语言映射关系对象
     */
    public static void bindEn(Map<String, String> enCache) {
        enMap.putAll(enCache);
    }

    /**
     * 获取简体中文对应的语言
     *
     * @param simple   简体字符串
     * @param language 语言对象
     * @return 转换后的语言对象
     */
    public static String acquire(String simple, String language) {
        LanguageType languageType = LanguageType.getByCode(language);
        return acquire(simple, languageType);
    }

    /**
     * 获取简体中文对应的语言
     *
     * @param simple       字符串
     * @param languageType 语言类型
     * @return 转换后的语言字符串
     */
    public static String acquire(String simple, LanguageType languageType) {
        if (Objects.isNull(languageType) || StringUtils.isEmpty(simple)) {
            return simple;
        }
        if (StringUtils.length(simple) > 1000) {
            return simple;
        }
        if (languageType.equals(LanguageType.ZH_TW)) {
            return zhMap.containsKey(simple) ? zhMap.get(simple) : I18nChineseHelper.convertToTraditionalChinese(simple);
        }
        if (languageType.equals(LanguageType.EN_US)) {
            return enMap.getOrDefault(simple, simple);
        }
        return simple;
    }

}
