package com.emily.infrastructure.security.utils;

import java.security.*;
import java.util.Base64;

/**
 * RSA公私钥生成
 */
public class RsaKeyPairUtils {
    /**
     * 算法
     */
    private static final String ALGORITHM = "RSA";

    /**
     * 生成密钥对
     *
     * @param keySize 算法长度
     * @return KeyPair 包含公钥和私钥
     */
    public static KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
        kpg.initialize(keySize);
        return kpg.generateKeyPair();
    }

    /**
     * 从 KeyPair 获取私钥字符串 (PEM 格式)
     */
    public static String getPrivateKeyString(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] encoded = privateKey.getEncoded();
        // 使用 PKCS#8 格式 (Java getEncoded() 默认返回 PKCS#8)
        return wrapPem(encoded, "PRIVATE KEY");
    }

    /**
     * 从 KeyPair 获取公钥字符串 (PEM 格式)
     */
    public static String getPublicKeyString(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] encoded = publicKey.getEncoded();
        // 使用 X.509 格式 (Java getEncoded() 默认返回 X.509)
        return wrapPem(encoded, "PUBLIC KEY");
    }

    /**
     * 辅助方法：添加 PEM 头尾标记
     * 格式:
     * -----BEGIN XXX-----
     * (Base64 内容)
     * -----END XXX-----
     */
    private static String wrapPem(byte[] keyBytes, String type) {
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        // 为了美观，通常每 64 个字符换行，但 Java 的 verify/sign 通常也接受单行长字符串。
        // 这里返回标准的单行或带换行的格式均可，取决于你的接收方。
        // 下面返回标准的多行格式（类似 OpenSSL 输出）：

        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN ").append(type).append("-----\n");

        // 每 64 个字符换行 (可选，如果对方不严格要求格式，直接返回 base64Key 也可以)
        for (int i = 0; i < base64Key.length(); i += 64) {
            int end = Math.min(i + 64, base64Key.length());
            sb.append(base64Key, i, end).append("\n");
        }

        sb.append("-----END ").append(type).append("-----\n");
        return sb.toString();
    }
}
