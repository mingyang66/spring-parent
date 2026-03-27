package com.emily.infrastructure.security.utils;


import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
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
    public static String encrypt(String data, String key, String iv) throws Throwable {
        if (isEmpty(data, key, iv)) {
            return data;
        }
        //1.生成秘钥
        final DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
        //2.秘钥工厂生成SecretKey
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        //3.密码器初始化
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
        //4.执行加密病转换为Base64
        final byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * DES解密字符串
     *
     * @param data 待解密字符串
     * @param key  解密密码，长度不能够小于8位
     * @param iv   偏移量
     * @return 解密后内容
     */
    public static String decrypt(String data, String key, String iv) throws Throwable {
        if (isEmpty(data, key, iv)) {
            return data;
        }
        //1.生成秘钥
        final DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        Key secretKey = keyFactory.generateSecret(desKeySpec);
        //2.容器初始化
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
        //3.执行解密
        byte[] decodedBytes = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static boolean isEmpty(String data, String key, String iv) {
        return StringUtils.isEmpty(data) || StringUtils.isEmpty(key) || StringUtils.isEmpty(iv);
    }
}

