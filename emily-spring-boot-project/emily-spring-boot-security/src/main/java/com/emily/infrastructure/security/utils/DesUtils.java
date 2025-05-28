package com.emily.infrastructure.security.utils;


import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

/**
 * DES加解密
 */
public class DesUtils {
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";

    /**
     * DES加密字符串
     *
     * @param data 待加密字符串
     * @param key  加密密码，长度不能够小于8位
     * @param iv   偏移量
     * @return 加密后内容
     */
    public static String encrypt(String data, String key, String iv) {
        try {
            if (StringUtils.isEmpty(data) || StringUtils.isEmpty(key)) {
                return data;
            }
            final DESKeySpec dks = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            final byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * DES解密字符串
     *
     * @param data 待解密字符串
     * @param key  解密密码，长度不能够小于8位
     * @param iv   偏移量
     * @return 解密后内容
     */
    public static String decrypt(String data, String key, String iv) {
        try {
            if (StringUtils.isEmpty(data) || StringUtils.isEmpty(key)) {
                return data;
            }
            final DESKeySpec dks = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            byte[] dataBytes = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
            return new String(cipher.doFinal(dataBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return data;
        }
    }
}

