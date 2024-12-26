package com.emily.infrastructure.language.i18n.registry;

import com.emily.infrastructure.language.convert.I18nChineseHelper;
import com.emily.infrastructure.language.i18n.LanguageType;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异常多语言缓存
 *
 * @author Emily
 * @since Created in 2022/8/9 7:38 下午
 */
public class I18nSimpleRegistry {
    /**
     * 简体-繁体
     */
    private static final Map<String, String> ZH_REGISTRY = new ConcurrentHashMap<>();
    /**
     * 简体-英文
     */
    private static final Map<String, String> EN_REGISTRY = new ConcurrentHashMap<>();

    /**
     * 简体-繁体 绑定
     */
    public static Map<String, String> getZhRegistry() {
        return ZH_REGISTRY;
    }

    /**
     * 简体-英文 绑定
     */
    public static Map<String, String> getEnRegistry() {
        return EN_REGISTRY;
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
            return getZhRegistry().containsKey(simple) ? getZhRegistry().get(simple) : I18nChineseHelper.convertToTraditionalChinese(simple);
        }
        if (languageType.equals(LanguageType.EN)) {
            return getEnRegistry().getOrDefault(simple, simple);
        }
        return simple;
    }

}
