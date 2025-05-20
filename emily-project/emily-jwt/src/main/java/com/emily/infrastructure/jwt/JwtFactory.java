package com.emily.infrastructure.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * jwt创建解析帮助类
 *
 * @author Emily
 * @since Created in 2023/5/14 1:23 PM
 */
public class JwtFactory {
    /**
     * 创建JWT Token字符串
     *
     * @param builder   入参
     * @param algorithm 算法
     * @return token字符串
     */
    public static String createJwtToken(JWTCreator.Builder builder, Algorithm algorithm) {
        // header:typ、alg 算法
        return builder.sign(algorithm);
    }

    /**
     * JWT字符串解码后的对象
     *
     * @param jwtToken  令牌
     * @param algorithm 算法
     * @return 解析后的jwt token对象
     */
    public static DecodedJWT verifyJwtToken(String jwtToken, Algorithm algorithm) {
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        return jwtVerifier.verify(jwtToken);
    }
}
