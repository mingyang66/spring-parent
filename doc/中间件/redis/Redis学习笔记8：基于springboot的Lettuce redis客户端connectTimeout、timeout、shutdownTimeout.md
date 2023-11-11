#### Redis学习笔记8：基于springboot的Lettuce redis客户端connectTimeout、timeout、shutdownTimeout

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.3.9</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、lettuce redis建立连接超时时间connectTimeout，默认：10S

```java
        ClientOptions.Builder builder = this.initializeClientOptionsBuilder(properties);
        Duration connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }
```

在io.lettuce.core.SocketOptions类中，connectTimeout参数用于设置连接超时时间。

连接超时时间是指在尝试建立连接时，等待连接建立完成的最大时间。如果连接建立时间超过了设置的连接超时时间，那么连接将被认为是超时的，连接建立失败。

如果超时将会抛出如下异常：

```sh
Caused by: io.netty.channel.ConnectTimeoutException: connection timed out: /10.10.xx.xxx:26382
	at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe$1.run(AbstractNioChannel.java:261)
	... 10 more
Exception in thread "task-2865" org.springframework.data.redis.RedisConnectionFailureException: Unable to connect to Redis
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$ExceptionTranslatingConnectionProvider.translateException(LettuceConnectionFactory.java:1604)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$ExceptionTranslatingConnectionProvider.getConnection(LettuceConnectionFactory.java:1535)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$SharedConnection.getNativeConnection(LettuceConnectionFactory.java:1360)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$SharedConnection.getConnection(LettuceConnectionFactory.java:1343)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory.getSharedConnection(LettuceConnectionFactory.java:1061)
	at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory.getConnection(LettuceConnectionFactory.java:400)
	at org.springframework.data.redis.core.RedisConnectionUtils.fetchConnection(RedisConnectionUtils.java:195)
	at org.springframework.data.redis.core.RedisConnectionUtils.doGetConnection(RedisConnectionUtils.java:144)
	at org.springframework.data.redis.core.RedisConnectionUtils.getConnection(RedisConnectionUtils.java:105)
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:393)
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:373)
	at org.springframework.data.redis.core.AbstractOperations.execute(AbstractOperations.java:97)
	at org.springframework.data.redis.core.DefaultValueOperations.set(DefaultValueOperations.java:253)
	at com.emily.infrastructure.test.controller.RedisController.lambda$get2$3(RedisController.java:104)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	at java.base/java.lang.Thread.run(Thread.java:833)
```

##### 二、lettuce redis命令操作超时时间timeout，默认：60S

```java
    private void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, RedisProperties properties) {
        if (this.isSslEnabled(properties)) {
            builder.useSsl();
        }
				//命令操作超时时间，默认：60s
        if (properties.getTimeout() != null) {
            builder.commandTimeout(properties.getTimeout());
        }
     }
```

LettuceClientConfigurationBuilder的默认值设置：

```java
	class LettuceClientConfigurationBuilder {
		...
		Duration timeout = Duration.ofSeconds(RedisURI.DEFAULT_TIMEOUT);
  }
```



##### 三、lettuce redis管理连接超时时间shutdownTimeout，默认：100ms

```java
    private void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, RedisProperties properties) {
     	  ...
        if (properties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = properties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());
            }
        }
     }
```

LettuceClientConfigurationBuilder默认值设置：

```java
	class LettuceClientConfigurationBuilder {
		...
		Duration shutdownTimeout = Duration.ofMillis(100);
  }
```

