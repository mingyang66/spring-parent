#### Druid数据库配置密码加密openssl生成和代码生成两种方案

### 方案一：openssl生成秘钥并加密

##### 1.生成私钥PKCS#1

```sh
openssl genrsa -out private_key.pem 2048
```



##### 2.从私钥中提取出公钥

```sh
openssl rsa -pubout -in private_key.pem -out public_key.pem
```



##### 3.对第一步中生成的PKCS#1私钥进行编码生成PKCS#8的私钥

```sh
openssl pkcs8 -topk8 -inform PEM -in private_key.pem -outform PEM -nocrypt -out private_key_pkcs8.pem
```



##### 4.新建security.txt文件，将密码添加到文件中，对字文件签名

```sh
#将密码123写入security.txt文件
echo 123>security.txt
#对密码文件进行签名
openssl rsautl -sign -inkey private_key_pkcs8.pem < security.txt > sign.txt
```

上述名利签名的密码会默认带一个换行符\n，这样的话oracle数据库可以在连接的时候自动去除，但是mysql就嗝屁了，如何去除换行符，命令如下：

```sh
cat security.txt | tr -d '\n' | openssl rsautl -sign -inkey private_key_pkcs8.pem > sign.txt
```



##### 5.验证签名是否正确，输出文件与签名源文件相同则说明签名正确

```sh
openssl rsautl -verify -inkey public_key.pem -keyform PEM -pubin -in sign.txt > security_bak.txt
```



##### 6.将签名转换成base64字符串，此字符串即密码签名

```sh
base64 sign.txt
```

### 方案二：Java代码生成秘钥并加密

```java
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
        String password = "123";
        String[] arr = genKeyPair(512);
        System.out.println("privateKey:" + arr[0]);
        System.out.println("publicKey:" + arr[1]);
        System.out.println("password:" + encrypt(arr[0], password));
    }
```



### 数据库consul配置文件做如下修改

使用密码签名字符串修改如下密码配置（embase替换为对应数据库标识）：

```properties
spring.emily.datasource.druid.mysql.password=密码签名字符串
```

新增如下配置，需将公钥替换为对应环境的公钥（embase替换为对应数据库标识）：

```properties

#秘钥属性配置  
spring.emily.datasource.druid.mysql.connection-properties=config.decrypt=true;config.decrypt.key=${spring.emily.datasource.druid.mysql.public-key}
#回调类
spring.emily.datasource.druid.mysql.password-callback-class-name=com.alibaba.druid.util.DruidPasswordCallback
#开启配置过滤器，用来读取公钥
spring.emily.datasource.druid.mysql.filters=config
#公钥
spring.emily.datasource.druid.mysql.public-key=公钥
#设置JDBC驱动执行Statement语句的秒数，如果超过限制，则会抛出SQLTimeoutException，默认：0 单位：秒 无限制
spring.emily.datasource.druid.mysql.query-timeout=10
#设置JDBC驱动执行N个Statement语句的秒数（事务模式），如果超过限制，则会抛出SQLTimeoutException，默认：0 单位：秒 无限制
spring.emily.datasource.druid.mysql.transaction-query-timeout=10  
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
