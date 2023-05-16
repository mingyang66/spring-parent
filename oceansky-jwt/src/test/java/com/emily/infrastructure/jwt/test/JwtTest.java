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
    public static final String publicKey1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkBpftIcwYL0qm0ecwdBX\n" +
            "h0oRE2vXFgwWLJCnottOuYVIx7OLFyow9ebyMF98PBU/qEEb3tsmZu1bzpPzLsyH\n" +
            "CG60t3Irebg+K/9Vir2WtEnqHF+2T5jWJkfRXZycbGoult449ZIdXbKIJUtYFrTf\n" +
            "BNHS1LaWmYGhCkBFIP/81ZGTvNZriwbhgusduaZmQtwqWaBSw4vTyscaE6vTJFyL\n" +
            "oe1N0FHVd7DKm/KMZEvQtTwuKAx/4qe3qYZR9tbcQ7Od7Dw+jCsiZmrrwI6C4r33\n" +
            "LFByDM+Elrq0yfnzfas8AdLmPY+Ihqr9iYFX2t6hsC9A4L0YX+gHer52L6+BlhNv\n" +
            "pwIDAQAB";
    public static final String privateKey1 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQGl+0hzBgvSqb\n" +
            "R5zB0FeHShETa9cWDBYskKei2065hUjHs4sXKjD15vIwX3w8FT+oQRve2yZm7VvO\n" +
            "k/MuzIcIbrS3cit5uD4r/1WKvZa0SeocX7ZPmNYmR9FdnJxsai6W3jj1kh1dsogl\n" +
            "S1gWtN8E0dLUtpaZgaEKQEUg//zVkZO81muLBuGC6x25pmZC3CpZoFLDi9PKxxoT\n" +
            "q9MkXIuh7U3QUdV3sMqb8oxkS9C1PC4oDH/ip7ephlH21txDs53sPD6MKyJmauvA\n" +
            "joLivfcsUHIMz4SWurTJ+fN9qzwB0uY9j4iGqv2JgVfa3qGwL0DgvRhf6Ad6vnYv\n" +
            "r4GWE2+nAgMBAAECggEASKsUIjyeV9ptFvspAM/oo8/lBo82Wubjc7vK3aSMcZ/W\n" +
            "EObouFjNcePxtBUi2EW64UVcIakQF42Q437Wfn4jhkwVlADeDbasm5FaeOmcivRP\n" +
            "O9nEXSVssMc8vGFSvJVbQzdzL9tsNajnYS86j9DMOmj5Uc116pllNX8tnTOaM7RB\n" +
            "BRZYeXIqWGRpUz1cu1RyNl7AK0pCXjoRO/DoEPG+D1apgo72RUSClHavl8xuG7j0\n" +
            "E5Ry0ARo7aUgmIUQZUjFXRJKfIrpeyicwEIoqkvFA9rYubDjzGP2NniU1yLzJVz/\n" +
            "ymARCDFwvhc8nbvyTEQVUlZxYjgeiXH7i1wagOMleQKBgQDJgIPHGHHRE+hVO7uP\n" +
            "wCQcIQ3o0ifWhfIN1vfHHFcVj1KTj6r3iSuxG49upE5EZ01imEBvh9M5ncuIYkqU\n" +
            "KYhz66UL5a2qA8OiSTYN00L2ULN/eiEi6VOE0iloKDtWIyIZUHejGPr6bwAlKJ3k\n" +
            "4PDqpOcAFn8K8X1+d+Mxhy24/QKBgQC3E7MXljuFbQ+NBygt53qARJIlc6shxbk4\n" +
            "Z7lSb8Ib7FlEQzxAVAymO33U/MIZAMAccyfj+0+9liDZC05Ackdmt54WaNIOpKY3\n" +
            "/fca7m6vR3R6QbzY8QU50VNzVLHyTsJ7zHHbH8oUAlRP0l8aFgwjQuUR9/TMGHeH\n" +
            "9HfTzbGOcwKBgGMYZrY1GVQ/PBUeqSEK1zdWMib7o0fm26FexMAQ+erKb0vObcAK\n" +
            "n4gcC6/X8f3F59LDGX1ACOre5UePPyoaOtb3XlW5gGyKKV3YL1MhQF8uVPguMbmf\n" +
            "kclSKbblgjjcUlqsrglxsYwTpriffvcegJyEuG3comHZXWQixYKH/j5pAoGBAKjM\n" +
            "pxIWWKb7GZ2Efc1lYpOlpcKWVbF75v1U1ZBmruikEOJZw9HkLQ2QSML6kdQP1xHk\n" +
            "M2GHM8ywAXVdcTcGp0LUBhTPbO5HbWKu7QmN0cwW7BRVTFQSVikrEfCCpNevOq5+\n" +
            "oHPTabTtOOOoKjuZHHPel+rrXnBYFs3p4BoPy6h1AoGATSSnCXsq2IyUMqXx8Tiq\n" +
            "P3zEM/3loVll4T5tbnKIxFeTThKWQLwOGL+nnSZhYT6xNSr1tl82zXObLzlDiT+k\n" +
            "Yfgomne+ivO3JqO8Os3dGFJiHWZpWRXWp812J+f/UnwZqlEXuSsS+ILd7tqNmWVI\n" +
            "I2rXOoEPfVzVTQBvbdeyixY=";

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
