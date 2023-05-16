package com.emily.infrastructure.jwt.test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.emily.infrastructure.jwt.JwtFactory;
import com.emily.infrastructure.jwt.RsaAlgorithmFactory;
import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 1:52 PM
 */
public class JwtTest {
    public static final String publicKey1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjnc5RxOVewpwzvb131oz\n" +
            "I4120riYDjebhyd0eYyPvN2VSNg8z43tqznvj+lG7Xn6Ij9qU7ydxN3GDK6xRdjS\n" +
            "HW2Tvu47Vm+ZpeNfuuhhBHlQ0zrDbh/fuLP/WH7a/ovjvf/Q3vqk/bakreT2Mm5I\n" +
            "Rtns53+yNDobvlnLXYgLQrwTFg2Ytarmv1wpzsaqIdW0p8GkLNvCF8ZONqvEb3Cb\n" +
            "O30jb5xEx3d2xhGGVobg6BpmK7OxY6dr5OCs9yk4RdVM5GG7lan8aa6kwWDyO9jB\n" +
            "ygb4r3+31Cr5xuz3ojdApAImeC709j6aX/zosm26+sAFK8sGhWMYVJBu9rKKIiyh\n" +
            "dQIDAQAB";
    public static final String privateKey1 = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCOdzlHE5V7CnDO\n" +
            "9vXfWjMjjXbSuJgON5uHJ3R5jI+83ZVI2DzPje2rOe+P6UbtefoiP2pTvJ3E3cYM\n" +
            "rrFF2NIdbZO+7jtWb5ml41+66GEEeVDTOsNuH9+4s/9Yftr+i+O9/9De+qT9tqSt\n" +
            "5PYybkhG2eznf7I0Ohu+WctdiAtCvBMWDZi1qua/XCnOxqoh1bSnwaQs28IXxk42\n" +
            "q8RvcJs7fSNvnETHd3bGEYZWhuDoGmYrs7Fjp2vk4Kz3KThF1UzkYbuVqfxprqTB\n" +
            "YPI72MHKBvivf7fUKvnG7PeiN0CkAiZ4LvT2Pppf/Oiybbr6wAUrywaFYxhUkG72\n" +
            "sooiLKF1AgMBAAECggEBAIlgdh8Px2jqXHV2twk1lXKHCzmPPzEWdicR6ML8w+4/\n" +
            "TaD8w0bxeWlPaK7BJ9//azBzLjio/QnFQSEho1fTCGnQLFRErXtgCi1o3/r/8e26\n" +
            "fjHxzFn46mbVSzkuukYS5v1kHSmnUEpHQO1eh/mBVrjblBJ3lIPANNiNBgmfEhy6\n" +
            "hXSC9HMYvJmui0pn1oRBQMG25MwfIxhDdcRn1uJnquhN5BzFplq75BUSYB+MNu3S\n" +
            "3jlg0vmkEMNcY+gLtxa4B771atvBgVqs4+8233Qb5LzyDj7H2n+XQ5qxwh6AmqF/\n" +
            "wZ1eK9b9iDhpUjE6BQm2ssgDDQ79zNQsIqc+amaA4lECgYEA0NSyCIOgsDf9Otk8\n" +
            "ghLSc85lMd3pLWC+432nIue3K/vm/KokmBN9pv27L/t7ttQsw91rNnb/X6Nvgjgs\n" +
            "19kUlD25qZfPtKB45q51nU/A1Mwkau1lOcdTT+0CVnKqab4wQsYcekpzG/sLv8Bs\n" +
            "PuviIMYbIqDhtAVZkIcmYX56olcCgYEArqUSnGeWfQlwtG2UQ4X2xcE8RbQ4VyTU\n" +
            "g2a62+swCg6qLWduXHH8YGlf3DSXWPZyW24xGNWWIhmwp82mzyZqr8aKWHjqzbUo\n" +
            "M7HyIFytrPOufrfKHhhOsrTm7pKoAhqmZxXvgcvCSxYCHVErHlqelTc1NW/UB9wy\n" +
            "lGd5SoiW8xMCgYEAk8qDeM0W8r+dIoHNdcy2Tij6qxD+zhOS5NLvbx+IHcO31Ibh\n" +
            "QRNCMOWPDUUwZ9K/H5rbHn+W+etjpkf1TIkgLE2G0QRUheOvzKoZKMzhjngvKdF6\n" +
            "eyqaxozYw6+A9TcZVph4XP/FeT7xMLKQqPMtZL5vQ9GSCmJi4YsUWZk2Vx8CgYEA\n" +
            "iLobc+91VbbKUbdoV+TNacz6zudlJHRlG+qVaA8csQMCIEHVmPJ0T1awAcn6o19t\n" +
            "8D64mWS+ATxznSvX6F1/MNYTAWjJCvtE2hP6J3PnVHwCpJpm1mDZW/dcxHJ0rhEH\n" +
            "LbVyqP9IwVgdl64wP7daaB+svIUsTmsJ/j10K/H7m5kCgYAjh8MI09EXKtcURjSc\n" +
            "6SbDXInC4lZmpfky8TVfkOB7m8LYVbr+6/Ds23H1/U5wWsfSv+eL+xsOlWWV0DPa\n" +
            "1HEzQISBrw+TfF1RZTOo1zGKTBQGsM5wJj4kpX1XH3vs+oPzSR3MqX6cFPQwbygD\n" +
            "PCa7aPmgETjjpTFKGKvnkGj5KQ==";

    @Test
    public void test() throws InvalidKeySpecException, NoSuchAlgorithmException {
        RSAPublicKey publicKey = RsaAlgorithmFactory.getPublicKey(publicKey1);

        //Security.addProvider(new BouncyCastleProvider());
        RSAPrivateKey privateKey = RsaAlgorithmFactory.getPrivateKey(privateKey1);
        System.out.println(privateKey.getFormat());
        Map<String, Object> headers = new HashMap<>();
        headers.put("ip", "123.12.123.25.12");
        JWTCreator.Builder builder = JWT.create()
                //JWT唯一标识 jti
                .withJWTId(UUID.randomUUID().toString())
                .withHeader(headers)
                .withClaim("username", "田润叶")
                .withClaim("password", "不喜欢")
                //发布者 iss
                .withIssuer("顾养民")
                //发布时间 iat
                .withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                //受众|收件人 aud
                .withAudience("田海民", "孙玉婷")
                //指定JWT在指定时间之前不得接受处理  nbf
                .withNotBefore(Date.from(LocalDateTime.now().plusMinutes(-1).atZone(ZoneId.systemDefault()).toInstant()))
                //JWT的主题 sub
                .withSubject("令牌")
                //JWT的密钥ID(实际未用到)，用于指定签名验证的密钥 kid  com.auth0.jwt.algorithms.RSAAlgorithm.verify
                .withKeyId("sd")
                //JWT过期时间 exp
                .withExpiresAt(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());
        String jwtToken = JwtFactory.createJwtToken(builder, Algorithm.RSA256(publicKey, privateKey));
        Assert.assertNotNull(jwtToken);

        DecodedJWT jwt = JwtFactory.verifyJwtToken(jwtToken, Algorithm.RSA256(publicKey, privateKey));
        Assert.assertEquals(jwt.getClaim("username").asString(), "田润叶");
        Assert.assertEquals(jwt.getClaim("password").asString(), "不喜欢");
        Assert.assertEquals(jwt.getHeaderClaim("ip").asString(), "123.12.123.25.12");
        Assert.assertEquals(jwt.getIssuer(), "顾养民");
        Assert.assertEquals(jwt.getAudience().get(0), "田海民");
        Assert.assertEquals(jwt.getAudience().get(1), "孙玉婷");
    }
}
