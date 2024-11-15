package com.emily.infrastructure.language.convert;

import org.apache.commons.lang3.StringUtils;

/**
 * 多语言枚举类
 *
 * @author Emily
 * @since Created in 2022/8/17 4:45 下午
 */
public enum LanguageType {
    ZH_CN("zh-CN", "简体"),
    ZH_TW("zh-TW", "繁体"),
    EN_US("en-US", "英文");

    private final String code;
    private final String name;

    LanguageType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LanguageType getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return ZH_CN;
        }
        for (LanguageType languageType : LanguageType.values()) {
            if (languageType.code.equals(code)) {
                return languageType;
            }
        }
        return ZH_CN;
    }

    public static LanguageType getByType(String type) {
        if (StringUtils.isEmpty(type)) {
            return ZH_CN;
        }
        for (LanguageType languageType : LanguageType.values()) {
            if (languageType.toString().equals(type)) {
                return languageType;
            }
        }
        return ZH_CN;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
