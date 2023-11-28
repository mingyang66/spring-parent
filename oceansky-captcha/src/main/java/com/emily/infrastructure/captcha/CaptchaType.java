package com.emily.infrastructure.captcha;

/**
 * 验证码类型
 *
 * @author Emily
 * @since Created in 2023/5/4 1:11 PM
 */
public enum CaptchaType {
    ALPHANUMERIC("0", "字母数字"),
    DIGIT("1", "数字"),
    LETTER("2", "字母");
    private String code;
    private String name;

    CaptchaType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
