#### Redis学习笔记10：基于spring的Lettuce redis客户端Pipelining管道

> Redis提供了对pipelining管道的支持，这包括在不等待回复的情况下向服务器发送多个命令，然后在一个步骤中读取回复。当需要连续发送多个命令时，管道化可以提高性能，例如：将许多元素添加到同一列表中。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、Redis回调接口RedisCallback

```java
public interface RedisCallback<T> {
	@Nullable
	T doInRedis(RedisConnection connection) throws DataAccessException;
}
```

> Redis回调接口和RedisTemplate的execution方法一起使用，通常作为方法实现中的匿名实现，通常用于将多个操作链接在一起（get/set/trim...）

示例如下：

```java
stringRedisTemplate.execute(new RedisCallback<Object>() {
    @Override
    public Object doInRedis(RedisConnection connection) throws DataAccessException {
        StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
        stringRedisConn.set("test", "1");
        stringRedisConn.setEx("test1",100,"2");
        String s = stringRedisConn.get("test");
        return null;
    }
});
```



##### 二、pipelined管道executePipelined方法RedisCallback回调

Spring Data Redis提供了几种RedisTemplate方法，用于在管道中运行命令。如果你不关心管道的操作结果，您可以使用标准的execute方法，为pipeline管道参数传递true，executePipelined方法在管道中运行提供的RedisCallback或SessionCallback并返回结果。如下示例：

```java
        List<Object> results = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                for (int i = 0; i < 100; i++) {
                    stringRedisConn.setEx("test" + i, 100, i + 100 + "");
                }
                return null;
            }
        });
```

> 注意：RedisCallback返回的值必须为null，应为为了返回流水线命令的结果，会丢弃该值。管道操作一次可以发送多条指令，提高操作的性能

##### 三、Lettuce驱动程序刷新策略

Lettuce驱动程序支持细粒度的刷新控制，允许在命令出现时刷新命令，缓冲或在连接关闭时发送命令。

```java
LettuceConnectionFactory factory = // ...
//本地缓存并在每3个命令后刷新
factory.setPipeliningFlushPolicy(PipeliningFlushPolicy.buffered(3));
```

LettuceConnectionFactory默认策略是每个命令都直接刷新发送到服务器：

```java
public class LettuceConnectionFactory{
    private PipeliningFlushPolicy pipeliningFlushPolicy = PipeliningFlushPolicy.flushEachCommand();
}
```

##### 四、lettuce pipelined管道一次可以发送多少指令

​		使用Lettuce执行Redis管道操作非常简单，可以一次性允许任意数量的指令。这种方式可以提高Redis的性能，特别是在需要执行多个指令的场景下。

##### 五、StringRedisTemplate的executePipelined方法是同步还是异步执行

- 在Spring Data Redis中，StringRedisTemplate的executePipelined方法是同步执行的。
- executePipelined方法允许在单个Redis连接上执行多个命令，并返回一个包含所有命令的结果列表。它会一次性发送多个命令到Redis服务器，并等待所有命令的响应。
- 调用executePipelined方法时，会阻塞当前线程，直到所有命令执行完毕并返回结果列表。因此，它是同步执行的，当前线程会等待所有命令的结果返回。