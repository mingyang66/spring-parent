#### 解锁新技能《如何使用JWT创建解析令牌，使用RSA非对称加密》

开源依赖pom引用地址

```xml
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-jwt</artifactId>
  <version>4.3.2</version>
</dependency>
```

##### 一、如何使用Java代码的方式生成RSA非对称秘钥

```java
public class RsaPemCreatorFactory {
    /**
     * 公钥文件名
     */
    private static final String PUBLIC_KEY_FILE = "publicKey.pem";
    /**
     * 私钥文件名
     */
    private static final String PRIVATE_KEY_FILE = "privateKey.pem";
    private static final String publicKeyPrefix = "PUBLIC KEY";
    private static final String privateKeyPrefix = "PRIVATE KEY";
    /**
     * 算法
     */
    public static final String ALGORITHM = "RSA";

    public static void create(String directory) throws NoSuchAlgorithmException, IOException {
        // algorithm 指定算法为RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        // 指定密钥长度为2048
        keyPairGenerator.initialize(1024);
        // 生成密钥
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 文件夹不存在，则先创建
        Files.createDirectories(Paths.get(directory));

        try (FileWriter writer = new FileWriter(String.join("", directory, PRIVATE_KEY_FILE));
             PemWriter pemWriter = new PemWriter(writer);
             FileWriter pubFileWriter = new FileWriter(String.join("", directory, PUBLIC_KEY_FILE));
             PemWriter pubPemWriter = new PemWriter(pubFileWriter)) {
            pemWriter.writeObject(new PemObject(privateKeyPrefix, keyPair.getPrivate().getEncoded()));
            pubPemWriter.writeObject(new PemObject(publicKeyPrefix, keyPair.getPublic().getEncoded()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

##### 二、如何创建RSAPublicKey、RSAPrivateKey对象

```java
public class RsaAlgorithmFactory {
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

```

##### 三、如何创建解析JWT Token

创建工厂方法：

```java
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
```

实际使用案例：

```java
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
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)