package com.emily.infrastructure.common.enums;

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
            code = LanguageType.ZH.getCode().toLowerCase();
        }
        LanguageType languageType = LanguageType.ZH;
        switch (code) {
            case "zh":
                languageType = LanguageType.ZH;
                break;
            case "ft":
                languageType = LanguageType.FT;
                break;
            case "en":
                languageType = LanguageType.EN;
                break;
            default:
                break;
        }
        return languageType;
    }
}
