package com.emily.infrastructure.captcha;

/**
 * 图形验证码建造器
 *
 * @author Emily
 * @since Created in 2023/5/4 10:18 AM
 */
public class CaptchaBuilder {
    /**
     * 验证码
     */
    private String code;
    /**
     * 验证码图片
     */
    private byte[] image;

    public CaptchaBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public CaptchaBuilder withImage(byte[] image) {
        this.image = image;
        return this;
    }

    public Captcha build() {
        return new Captcha(this.code, this.image);
    }
}
