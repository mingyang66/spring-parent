package com.emily.infrastructure.test.test.totp;


import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * @author :  Emily
 * @since :  2023/12/6 4:57 PM
 */
public class TimeBasedOTP {

    /**
     * 密码有效期，在有效期内，生成的所有密码都一样
     */
    private static final Duration TIME_STEP = Duration.ofSeconds(10);

    /**
     * 密码长度
     */
    private static final int PASSWORD_LENGTH = 6;
    private KeyGenerator keyGenerator;
    private TimeBasedOneTimePasswordGenerator totp;

    /*
     * 初始化代码块，Java 8 开始支持。这种初始化代码块的执行在构造函数之前
     * 准确说应该是 Java 编译器会把代码块拷贝到构造函数的最开始。
     */ {
        try {
            totp = new TimeBasedOneTimePasswordGenerator(TIME_STEP, PASSWORD_LENGTH);
            keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
            // SHA-1 and SHA-256 需要 64 字节 (512 位) 的 key; SHA512 需要 128 字节 (1024 位) 的 key
            keyGenerator.init(512);
        } catch (NoSuchAlgorithmException e) {
            //log.error("没有找到算法 {}", e.getLocalizedMessage());
        }
    }

    /**
     * @param time 用于生成 TOTP 的时间
     * @return 一次性验证码
     */
    public String createTotp(final String strKey, final Instant time) {
        Key key = new SecretKeySpec(Base64.getDecoder().decode(strKey), totp.getAlgorithm());
        try {
            return String.valueOf(totp.generateOneTimePassword(key, time));
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证 TOTP
     *
     * @param code 要验证的 TOTP
     * @return 是否一致
     */
    public boolean validateTotp(final String strKey, final String code) {
        Instant now = Instant.now().plusSeconds(10);
        return createTotp(strKey, now).equals(code);
    }

    public static void main(String[] args) {
        TimeBasedOTP util = new TimeBasedOTP();
        String key = "p3000002059";
        for (int i = 0; i < 60; i++) {
            String code = util.createTotp(key, Instant.now());
            System.out.println(i + " " + DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS) + ":" + " " + util.validateTotp(key, code));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}