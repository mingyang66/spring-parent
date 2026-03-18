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
    /**
     * 算法
     */
    private static final String ALGORITHM = "RSA";
    /**
     * Algorithm/Mode/Padding  算法/模式/填充
     */
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String TRANSFORMATION2 = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";


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
