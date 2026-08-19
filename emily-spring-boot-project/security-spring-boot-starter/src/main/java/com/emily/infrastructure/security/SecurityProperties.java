package com.emily.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 入参加解密配置类
 *
 * @author :  Emily
 * @since :  2024/10/31 上午10:12
 */
@ConfigurationProperties(prefix = SecurityProperties.PREFIX)
public class SecurityProperties {

    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emis.security";
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 私钥-1024
     */
    private String privateKey1024;
    /**
     * 公钥-1024
     */
    private String publicKey1024;
    /**
     * 私钥-2048
     */
    private String privateKey2048;
    /**
     * 公钥-2048
     */
    private String publicKey2048;
    /**
     * 私钥-4096
     */
    private String privateKey4096;
    /**
     * 公钥-4096
     */
    private String publicKey4096;

    /**
     * 判断是否开启加解密组件
     *
     * @return true-是，false-否
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否开启加解密组件
     *
     * @param enabled 是否开启
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrivateKey1024() {
        return privateKey1024;
    }

    public void setPrivateKey1024(String privateKey1024) {
        this.privateKey1024 = privateKey1024;
    }

    public String getPublicKey1024() {
        return publicKey1024;
    }

    public void setPublicKey1024(String publicKey1024) {
        this.publicKey1024 = publicKey1024;
    }

    public String getPrivateKey2048() {
        return privateKey2048;
    }

    public void setPrivateKey2048(String privateKey2048) {
        this.privateKey2048 = privateKey2048;
    }

    public String getPublicKey2048() {
        return publicKey2048;
    }

    public void setPublicKey2048(String publicKey2048) {
        this.publicKey2048 = publicKey2048;
    }

    public String getPrivateKey4096() {
        return privateKey4096;
    }

    public void setPrivateKey4096(String privateKey4096) {
        this.privateKey4096 = privateKey4096;
    }

    public String getPublicKey4096() {
        return publicKey4096;
    }

    public void setPublicKey4096(String publicKey4096) {
        this.publicKey4096 = publicKey4096;
    }
}
