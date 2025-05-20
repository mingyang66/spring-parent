package com.emily.infrastructure.language.convert;

/**
 * 简繁体转换枚举
 *
 * @author Emily
 */
public enum I18nType {
    /**
     * 简体转繁体
     */
    SIMPLE_TO_TRADITIONAL("s2t"),

    /**
     * 繁体转简体
     */
    TRADITIONAL_TO_SIMPLE("t2s");

    private final String code;

    I18nType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
