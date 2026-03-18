package com.emily.infrastructure.security.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA加解密工具，动态适配block长度
 */
public class RsaUtils {
    private static final Logger LOG = LoggerFactory.getLogger(RsaUtils.class);

    private static final String ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String TRANSFORMATION2 = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

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
    /**
     * 加密方法
     *
     * @param content 原始字符串
     * @param pubKey  公钥
     * @return Base64 密文
     */
    public static String encrypt(String content, String pubKey) {
        try {
            RSAPublicKey publicKey = loadPublicKeyFromString(pubKey);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            int encryptBlock = (publicKey.getModulus().bitLength() / 8) - 11;
            byte[] dataBytes = content.getBytes(StandardCharsets.UTF_8);
            byte[] resultBytes = doSegmentedOperation(dataBytes, cipher, encryptBlock);
            return Base64.getEncoder().encodeToString(resultBytes);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return content;
        }
    }

    /**
     * 解密方法
     *
     * @param base64Content Base64 密文
     * @return 原始字符串
     */
    public static String decrypt(String base64Content, String priKey) {
        try {
            RSAPrivateKey privateKey = loadPrivateKeyFromString(priKey);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            int decryptBlock = privateKey.getModulus().bitLength() / 8;
            byte[] dataBytes = Base64.getDecoder().decode(base64Content);
            byte[] resultBytes = doSegmentedOperation(dataBytes, cipher, decryptBlock);
            return new String(resultBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return base64Content;
        }
    }

    /**
     * 分段处理核心逻辑
     */
    private static byte[] doSegmentedOperation(byte[] data, Cipher cipher, int blockSize) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int length = data.length;
        int offset = 0;
        byte[] cache;

        while (length - offset > 0) {
            if (length - offset > blockSize) {
                cache = cipher.doFinal(data, offset, blockSize);
            } else {
                cache = cipher.doFinal(data, offset, length - offset);
            }
            out.write(cache, 0, cache.length);
            offset += blockSize;
        }
        out.close();
        return out.toByteArray();
    }

    /**
     * 从字符串加载公钥 (自动清洗 PEM 格式)
     */
    private static RSAPublicKey loadPublicKeyFromString(String keyStr) throws Exception {
        // 2. 清理 PEM 格式，只保留 Base64 内容 移除所有换行和空格
        String base64Key = keyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    /**
     * 从字符串加载私钥 (自动清洗 PEM 格式)
     */
    private static RSAPrivateKey loadPrivateKeyFromString(String keyStr) throws Exception {
        // 2. 清理 PEM 格式，只保留 Base64 内容 移除所有换行和空格
        String base64Key = keyStr
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        Security.addProvider(new BouncyCastleProvider());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }
}
