package com.emily.infrastructure.test.mainTest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.emily.infrastructure.common.object.UUIDUtils;

import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description :  JSON Web Token
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 1:44 PM
 */
public class JwtTest {
    public static void main(String[] args) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("deviceId", "sbid");
        String jwtToken = JWT.create()
                // 第一步header
                .withHeader(headers)
                // 第二部分PayLoad
                //  jwt唯一标识 jti
                .withJWTId(UUIDUtils.randomSimpleUUID())
                // 签发者 iss
                .withIssuer("server")
                // 签发时间 iat
                .withIssuedAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                .withClaim("username", "田晓霞")
                .withClaim("password", "123456")
                // 过期时间 exp
                .withExpiresAt(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant())
                // header:typ、alg 算法
                .sign(Algorithm.HMAC256("zheshimiyao"));
        System.out.println(jwtToken);

        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("zheshimiyao")).build();
        DecodedJWT verify = jwtVerifier.verify(jwtToken);
        System.out.println(verify.getHeaderClaim("deviceId"));
        System.out.println(verify.getClaim("username"));
        System.out.println(verify.getClaim("password"));
        System.out.println(verify.getExpiresAt());
    }


}
