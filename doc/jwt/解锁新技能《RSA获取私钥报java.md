#### 解锁新技能《RSA获取私钥报java.security.InvalidKeyException: IOException : algid parse error, not a sequence》

开源依赖pom引用：

```xml
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-jwt</artifactId>
  <version>4.3.1</version>
</dependency>
```



##### 获取RSAPrivateKey私钥对象

```java
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey.replace(N, "").replace(R, "").getBytes(StandardCharsets.UTF_8));
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }
```

如果上述代码解析的是PKCS8秘钥串是没有问题的，但是如果解析的是非PKCS8秘钥串就会报如下异常：

```shell
java.security.spec.InvalidKeySpecException: java.security.InvalidKeyException: IOException : algid parse error, not a sequence

	at java.base/sun.security.rsa.RSAKeyFactory.engineGeneratePrivate(RSAKeyFactory.java:251)
	at java.base/java.security.KeyFactory.generatePrivate(KeyFactory.java:390)
	at com.emily.infrastructure.jwt.RsaAlgorithmFactory.getPrivateKey(RsaAlgorithmFactory.java:56)
	at com.emily.infrastructure.jwt.test.JwtTest.test(JwtTest.java:52)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:59)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:56)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
	at org.junit.runners.BlockJUnit4ClassRunner$1.evaluate(BlockJUnit4ClassRunner.java:100)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:366)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:103)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:63)
	at org.junit.runners.ParentRunner$4.run(ParentRunner.java:331)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:79)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:329)
	at org.junit.runners.ParentRunner.access$100(ParentRunner.java:66)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:293)
	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:413)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:69)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater$1.execute(IdeaTestRunner.java:38)
	at com.intellij.rt.execution.junit.TestsRepeater.repeat(TestsRepeater.java:11)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:35)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:235)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:54)
Caused by: java.security.InvalidKeyException: IOException : algid parse error, not a sequence
	at java.base/sun.security.pkcs.PKCS8Key.decode(PKCS8Key.java:350)
	at java.base/sun.security.pkcs.PKCS8Key.decode(PKCS8Key.java:355)
	at java.base/sun.security.rsa.RSAPrivateCrtKeyImpl.<init>(RSAPrivateCrtKeyImpl.java:130)
	at java.base/sun.security.rsa.RSAPrivateCrtKeyImpl.newKey(RSAPrivateCrtKeyImpl.java:80)
	at java.base/sun.security.rsa.RSAKeyFactory.generatePrivate(RSAKeyFactory.java:356)
	at java.base/sun.security.rsa.RSAKeyFactory.engineGeneratePrivate(RSAKeyFactory.java:247)
	... 30 more
```

解决方案是添加Security.addProvider(new BouncyCastleProvider())代码，将非PKCS8秘钥串转换为PKCS8秘钥串：

```java
  public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey.replace(N, "").replace(R, "").getBytes(StandardCharsets.UTF_8));
        Security.addProvider(new BouncyCastleProvider());
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)