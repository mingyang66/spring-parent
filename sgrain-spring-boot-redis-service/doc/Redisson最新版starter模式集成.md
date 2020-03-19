### Redisson最新版starter模式集成

##### 前言

redisson-spring-boot-starter最新版本是3.12.3，支持两种配置方式，首先是完全兼容spring-boot-starter-data-redis的配置，第二可以使用redisson自己的配置方式。

##### 1.在maven pom文件中引入依赖

```
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>3.12.3</version>
        </dependency>
```

##### 2.在application配置文件中添加如下配置

```
# common spring boot settings

spring.redis.database=
spring.redis.host=
spring.redis.port=
spring.redis.password=
spring.redis.ssl=
spring.redis.timeout=
spring.redis.cluster.nodes=
spring.redis.sentinel.master=
spring.redis.sentinel.nodes=

```

这种方式是完全兼容springboot配置的方法；如果不想使用这种配置，而是想使用redisson自定义配置则可以在application配置文件中添加如下配置：

```
# Redisson settings

#path to config - redisson.yaml
spring.redis.redisson.config=classpath:redisson.yaml
```

redisson.yaml文件配置如下：

```
sentinelServersConfig:
  idleConnectionTimeout: 10000
  pingTimeout: 1000
  connectTimeout: 10000
  timeout: 3000
  retryAttempts: 3
  retryInterval: 1500
  reconnectionTimeout: 3000
  failedAttempts: 3
  password: null
  subscriptionsPerConnection: 5
  clientName: null
  loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> {}
  slaveSubscriptionConnectionMinimumIdleSize: 1
  slaveSubscriptionConnectionPoolSize: 50
  slaveConnectionMinimumIdleSize: 32
  slaveConnectionPoolSize: 64
  masterConnectionMinimumIdleSize: 32
  masterConnectionPoolSize: 64
  readMode: "SLAVE"
  sentinelAddresses:
  - "redis://127.0.0.1:26379"
  - "redis://127.0.0.1:26389"
  masterName: "mymaster"
  database: 0
threads: 0
nettyThreads: 0
codec: !<org.redisson.codec.JsonJacksonCodec> {}
"transportMode":"NIO"
```

redisson配置文件优先级高于springboot配置文件优先级；



参考：[配置方法-哨兵模式](https://github.com/redisson/redisson/wiki/2.-配置方法#27-哨兵模式)

参考：https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter#spring-boot-starter

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-redis-service/doc](https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-redis-service/doc)