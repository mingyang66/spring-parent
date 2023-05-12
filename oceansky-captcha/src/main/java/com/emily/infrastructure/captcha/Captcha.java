package com.emily.infrastructure.captcha;

/**
 * @Description :  图形验证码
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/3 10:16 AM
 */
public class Captcha {
    /**
     * 验证码
     */
    private String code;
    /**
     * 验证码图片
     */
    private byte[] image;

    public Captcha() {
    }

    public Captcha(String code, byte[] image) {
        this.code = code;
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
