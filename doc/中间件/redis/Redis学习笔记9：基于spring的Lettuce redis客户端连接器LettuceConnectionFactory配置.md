#### Redis学习笔记9：基于spring的Lettuce redis客户端连接器LettuceConnectionFactory配置

> Lettuce是一个基于Netty的开源连接器，由Spring Data Redis通过org.springframework.data.redis.connection.lettuce包提供支持。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、添加如下依赖到pom.xml

```xml
<dependencies>
  <!-- other dependency elements omitted -->
  <dependency>
    <groupId>io.lettuce</groupId>
    <artifactId>lettuce-core</artifactId>
    <version>6.2.6.RELEASE</version>
  </dependency>
</dependencies>

```

##### 二、如下示例展示如何通过Lettuce创建一个连接器工厂

```java
@Bean
public LettuceConnectionFactory redisConnectionFactory() {
  return new LettuceConnectionFactory(new RedisStandaloneConfiguration("server", 6379));
}
```

​		还有一些lettuce特有的连接参数可以调整。默认情况下所有的LettuceConnection实例是通过LettuceConnectionFactory工厂类创建，对于所有的非阻塞和非事务操作共享同一个线程安全的本地连接。每次使用专用连接时需要将shareNativeConnection设置为false。LettuceConnectionFactory也可以配置使用LettucePool用于池化阻塞和事务连接或者对所有连接使用池化技术可以将shareNativeConnection设置为false。

​		Lettuce与Netty本地传输工具相结合，允许您使用Unix域套接字与Redis通信。确保包含与运行时环境相匹配的本机传输依赖项。以下示例展示如何通过/var/run/redes.sock未Unix域套接字创建Lettuce连接工厂：

```java
  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(new RedisSocketConfiguration("/var/run/redis.sock"));
  }
```

> Netty目前支持操作系统本机传输的epoll(linux)和kqueue(BSD/macOS)接口