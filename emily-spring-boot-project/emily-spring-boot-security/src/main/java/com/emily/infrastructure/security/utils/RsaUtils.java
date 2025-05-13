package com.emily.infrastructure.security.utils;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密工具
 */
public class RsaUtils {

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 获取秘钥值
     */
    private static String getKey(String path) throws IOException {
        File file = new File(path);

        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder privateKeyStr = new StringBuilder();
        String privatekey = "";
        while ((privatekey = br.readLine()) != null) {
            privateKeyStr.append(privatekey);
        }
        br.close();
        return privateKeyStr.toString();

    }

    /**
     * RSA加密 数据超长对数据分段加密
     */
    public static String encrypt(String data, String publicKey) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] byteData = data.getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = Base64.decodeBase64(publicKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicK = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicK);
            int length = byteData.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (length - offSet > 0) {
                if (length - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(byteData, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(byteData, offSet, length - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            return Base64.encodeBase64String(encryptedData);
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * RSA解密  数据超长后对数据分段解密
     */
    public static String decrypt(String data, String privateKey) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] decodeData = Base64.decodeBase64(data);
            Security.addProvider(new BouncyCastleProvider());
            byte[] keyBytes = Base64.decodeBase64(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateK);
            int length = decodeData.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (length - offSet > 0) {
                if (length - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(decodeData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(decodeData, offSet, length - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return data;
        }
    }
}
