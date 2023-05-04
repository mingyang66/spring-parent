package com.emily.infrastructure.common.captcha;

/**
 * @Description :  验证码类型
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/4 1:11 PM
 */
public enum CaptchaType {
    ALPHANUMERIC("字母数字"),
    DIGIT("数字"),
    LETTER("字母");
    private String description;

    CaptchaType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
