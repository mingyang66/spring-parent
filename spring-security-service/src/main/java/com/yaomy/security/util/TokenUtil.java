package com.yaomy.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.util.JwtTokenUtil
 * @Author: 姚明洋
 * @Date: 2019/7/1 16:43
 * @Version: 1.0
 */
public class TokenUtil {
    /**
     * @Description token 过期时间
     * @Date 2019/7/5 9:53
     * @Version  1.0
     */
    private static Long expirationSeconds = 300L;
    /**
     * @Description 加密盐
     * @Date 2019/7/5 9:53
     * @Version  1.0
     */
    private static String salt = "_secret";
   /* private static InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("src/main/resources/jwt.jks"); // 寻找证书文件
    private static PrivateKey privateKey = null;
    private static PublicKey publicKey = null;

    static { // 将证书文件里边的私钥公钥拿出来
        try {
            // java key store 固定常量
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, "123456".toCharArray());
            // jwt 为 命令生成整数文件时的别名
            privateKey = (PrivateKey) keyStore.getKey("jwt", "123456".toCharArray());
            publicKey = keyStore.getCertificate("jwt").getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    /**
     * @Description 生成JWT用户令牌
     * @Date 2019/7/5 9:50
     * @Version  1.0
     */
    public static String generateToken(String subject) {
        return Jwts.builder()
                .setClaims(null)
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
                // 不使用公钥私钥
                .signWith(SignatureAlgorithm.HS512, salt)
              //  .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }
    /**
     * @Description 解析token令牌，获取用户subject
     * @Date 2019/7/5 10:19
     * @Version  1.0
     */
    public static String parseToken(String token) {
        String subject = null;
        try {
            Claims claims = Jwts.parser()
                    // 不使用公钥私钥
                    .setSigningKey(salt)
                    //.setSigningKey(publicKey)
                    .parseClaimsJws(token)
                    .getBody();
            subject = claims.getSubject();
        } catch (Exception e) {
        }
        return subject;
    }

}
