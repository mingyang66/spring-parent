package com.emily.infrastructure.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.time.DateUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/12/02
 */
public class JWTTest {
    public static void main(String[] args) {
        String token = JWT.create()
                // Header
                .withHeader(new HashMap<>())
                // Payload
                .withClaim("username", "Emily")
                .withClaim("password", "123456")
                .withClaim("age", "12")
                // 过期时间
                .withExpiresAt(DateUtils.addMinutes(new Date(), 1))
                // 签名用的秘钥
                .sign(Algorithm.HMAC256("123456"));
        System.out.println(token);
        System.out.println(token.length());

        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("123456")).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        Claim username = decodedJWT.getClaim("username");
        Claim password = decodedJWT.getClaim("password");
        System.out.println(username.asString());
        System.out.println(password.asString());
        System.out.println(decodedJWT.getExpiresAt());
    }
}
