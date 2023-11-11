#### Redis学习笔记11：基于spring的Lettuce redis客户端普通指令、RedisCallback、SessionCallback建立连接数

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、StringRedisTemplate普通指令建立连接数

```java
        stringRedisTemplate.opsForValue().set("test1", "2", 10, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set("test2", "2", 10, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set("test3", "2", 10, TimeUnit.SECONDS);
```

执行上述三条指令，日志如下：

```sh
2023-11-01 21:40:10.716 DEBUG default --- [se-nio-8080-exec-7] o.s.d.r.core.RedisConnectionUtils   :143  : Fetching Redis Connection from RedisConnectionFactory 
2023-11-01 21:40:10.722 DEBUG default --- [se-nio-8080-exec-7] o.s.d.r.core.RedisConnectionUtils   :379  : Closing Redis Connection 
2023-11-01 21:40:10.723 DEBUG default --- [se-nio-8080-exec-7] o.s.d.r.core.RedisConnectionUtils   :143  : Fetching Redis Connection from RedisConnectionFactory 
2023-11-01 21:40:10.729 DEBUG default --- [se-nio-8080-exec-7] o.s.d.r.core.RedisConnectionUtils   :379  : Closing Redis Connection 
2023-11-01 21:40:10.730 DEBUG default --- [se-nio-8080-exec-7] o.s.d.r.core.RedisConnectionUtils   :143  : Fetching Redis Connection from RedisConnectionFactory
2023-11-01 21:40:10.736 DEBUG default --- [se-nio-8080-exec-7] o.s.d.r.core.RedisConnectionUtils   :379  : Closing Redis Connection 
```

> 如上述日志所示，每条指令都会建立一个redis连接，指令执行完成后关闭。看日志确实是建立了三条连接，但是我有个疑惑lettuce redis客户端默认不是shareNativeConnection为true公用一条本地物理连接？那就通过netstat -an|grep ip地址查看本地建立连接数发现确实只有一条，通过debug代码org.springframework.data.redis.core.RedisConnectionUtils#doCloseConnection发现其实不是真正的关闭物理连接，综上分析：多条普通指令是公用一个本地物理连接的。



##### 二、StringRedisTemplate管道pipelined RedisCallback回调建立连接数

```
       List<Object> results = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
                for (int i = 0; i < 10; i++) {
                    stringRedisConn.setEx("test" + i, 100, i + 100 + "");
                }
                return null;
            }
        });
```

执行上述管道指令，管道中发送10条指令，执行日志结果如下：

```sh
2023-11-01 21:54:11.475 DEBUG default --- [se-nio-8080-exec-8] o.s.d.r.core.RedisConnectionUtils   :143  : Fetching Redis Connection from RedisConnectionFactory 
2023-11-01 21:54:11.559 DEBUG default --- [se-nio-8080-exec-8] o.s.d.r.core.RedisConnectionUtils   :379  : Closing Redis Connection 
```

发送指令SETEX日志一共有10条：

```sh
2023-11-01 21:54:11.534 DEBUG default --- [se-nio-8080-exec-8] io.lettuce.core.RedisChannelHandler :202  : dispatching command AsyncCommand [type=SETEX, output=StatusOutput [output=null, error='null'], commandType=io.lettuce.core.protocol.Command] 
```

> 管道建立一个连接，封装多条指令，一次性提交后关闭连接，返回执行结果集合，能有效提升性能。可以通过netstat -an|grep ip地址观察本地连接的建立和销毁，可以发现即使shareNativeConnection为true，也会新建一条新的本地物理连接，管道中的所有指令执行完成后则会立马关闭新建的连接。

##### 三、StringRedisTemplate管道pipelined SessionCallback回调建立连接数

```java
        stringRedisTemplate.executePipelined(new SessionCallback<String>() {
            @Override
            public String execute(RedisOperations operations) throws DataAccessException {
                operations.opsForValue().set("test","t",10,TimeUnit.SECONDS);
                operations.opsForValue().set("test","t",10,TimeUnit.SECONDS);
                operations.opsForValue().set("test","t",10,TimeUnit.SECONDS);
                operations.opsForValue().set("test","t",10,TimeUnit.SECONDS);
                return null;
            }
        });
```

执行日志如下：

```sh
2023-11-03 14:40:23.527 DEBUG default --- [se-nio-8080-exec-7] o.s.d.r.core.RedisConnectionUtils   :143  : Fetching Redis Connection from RedisConnectionFactory 
2023-11-03 14:40:27.007 DEBUG default --- [se-nio-8080-exec-7] o.s.d.r.core.RedisConnectionUtils   :379  : Closing Redis Connection 
```

> 通过日志可以看到管道建了一条连接，指令执行完成后则直接关闭；通过netstat -an|grep ip地址  观察到新建了一条本地物理连接，指令执行完成后则直接关闭了连接。

