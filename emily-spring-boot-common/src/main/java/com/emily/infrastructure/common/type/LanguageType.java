package com.emily.infrastructure.common.type;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description :  多语言枚举类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/8/17 4:45 下午
 */
public enum LanguageType {
    ZH("zh", "简体"),
    FT("ft", "繁体"),
    EN("en", "英文");

    private String code;
    private String desc;

    LanguageType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static LanguageType getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return ZH;
        }
        for (LanguageType languageType : LanguageType.values()) {
            if (languageType.code.equals(code)) {
                return languageType;
            }
        }
        throw new IllegalArgumentException("非法语言类型");
    }

    public static LanguageType getByType(String type) {
        if (StringUtils.isEmpty(type)) {
            return ZH;
        }
        for (LanguageType languageType : LanguageType.values()) {
            if (languageType.toString().equals(type)) {
                return languageType;
            }
        }
        throw new IllegalArgumentException("非法语言类型");
    }

}
