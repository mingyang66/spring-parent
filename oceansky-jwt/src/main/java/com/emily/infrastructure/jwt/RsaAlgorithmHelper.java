package com.emily.infrastructure.jwt;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * PKCS8：全名《Public-Key Cryptography Standards (PKCS) #8: Private-Key Information Syntax Specification》最新版本1.2，
 * 从名称上可以看出它是一个专门用来存储私钥的文件格式规范。
 *
 * @Description :  RSA算法工具类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 10:40 AM
 */
public class RsaAlgorithmHelper {
    public static final String N = "\n";
    public static final String R = "\r";
    public static final String ALGORITHM = "RSA";

    /**
     * 获取公钥对象
     *
     * @param publicKey 公钥字符串
     * @return 公钥对象
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (publicKey == null || publicKey.length() == 0) {
            throw new IllegalArgumentException("非法参数");
        }
        byte[] keyBytes = Base64.getDecoder().decode(publicKey.replace(N, "").replace(R, "").getBytes(StandardCharsets.UTF_8));
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    }

    /**
     * 获取私钥对象
     *
     * @param privateKey 私钥字符串
     * @return 私钥对象
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey.replace(N, "").replace(R, "").getBytes(StandardCharsets.UTF_8));
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }
}
