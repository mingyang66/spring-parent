##### 一、POM依赖

```xml
            <dependency>
                <groupId>io.github.mingyang66</groupId>
                <artifactId>emily-spring-boot-redis</artifactId>
                <version>${revision}</version>
            </dependency>
```

##### 二、属性配置

- spring.emily.redis.default-config默认配置必须配置；
- spring.emily.redis.config.test.client-type默认lettuce，默认只支持lettuce;
- spring.emily.redis.config.test.protocol-version版本协议 RESP2（Redis 2 to Redis 5）、RESP3（Redis 6）

```properties
# 是否开启Redis多数据源配置，默认：true
spring.emily.redis.enabled=true
# 是否开启容器监听器，默认：false
spring.emily.redis.listener=true
# Redis默认配置标识，无默认值
spring.emily.redis.default-config=test
# Redis默认使用的客户端类型，LETTUCE|JEDIS
spring.emily.redis.client-type=lettuce
# Redis默认使用的客户端类型，LETTUCE|JEDIS
spring.emily.redis.config.test.client-type=lettuce
# Redis协议版本
spring.emily.redis.config.test.protocol-version=resp2
# Redis连接工厂使用的数据库索引
spring.emily.redis.config.test.database=10
# Redis服务器的登录用户名
spring.emily.redis.config.test.username=
# Redis服务器的登录密码
spring.emily.redis.config.test.password=123456789
# 设置命令操作超时时间，默认：PT1M
spring.emily.redis.config.test.timeout=PT3S
# 建立连接超时时间，默认：10S
spring.emily.redis.config.test.connect-timeout=PT3S
# 用于通过sentinel进行身份验证的登录用户名
spring.emily.redis.config.test.sentinel.username=mymaster
# 使用sentinel进行身份验证的密码
spring.emily.redis.config.test.sentinel.password=
# 主节点名称（Sentinel哨兵配置中定义的主节点名称）
spring.emily.redis.config.test.sentinel.master=mymaster
# Redis哨兵节点配置，逗号分隔的  host:port
spring.emily.redis.config.test.sentinel.nodes=10.10.xx.xx:26380,10.10.xx.xx:26381,10.10.xx.82:26382
# 关闭连接超时时间，默认：100ms
spring.emily.redis.config.test.lettuce.shutdown-timeout=PT0.1S
# 是否开启连接校验，默认：false
spring.emily.redis.config.test.lettuce.validate-connection=false
# 是否开启共享本地物理连接，默认：true
spring.emily.redis.config.test.lettuce.share-native-connection=true
# 是否提前初始化连接，默认：false
spring.emily.redis.config.test.lettuce.eager-initialization=false
# 是否开启连接池(如果引用commons-pool2默认开启)
spring.emily.redis.config.test.lettuce.pool.enabled=false
# 连接池中允许的最大活动连接数，默认值为8。超过这个数值的连接将被阻塞等待。
spring.emily.redis.config.test.lettuce.pool.max-active=8
# 连接池中允许的最大空闲连接数，默认值为8。超过这个数值的空闲连接将被关闭。
spring.emily.redis.config.test.lettuce.pool.max-idle=8
# 连接池中保持的最小空闲连接数，默认值为0。当连接池中的连接数低于该值时，连接池会创建新的连接。
spring.emily.redis.config.test.lettuce.pool.min-idle=0
# 连接池资源耗尽时，连接尝试分配阻塞时间,超时即抛出异常。使用负值无限期阻塞。
spring.emily.redis.config.test.lettuce.pool.max-wait=-1
# 空闲对象逐出器线程运行之间的时间。如果为正，则启动空闲对象驱逐线程，否则不执行空闲对象驱逐。，默认：-1
spring.emily.redis.config.test.lettuce.pool.time-between-eviction-runs=PT0.1S
# 对象在池中最小可空闲时间, 默认：30分钟
spring.emily.redis.config.test.lettuce.pool.min-evictable-idle-duration=PT1M
```

##### 三、使用案例

- 支持通过DataRedisFactory工厂类获取XXRedisTemplate模板对象；
- 支持通过依赖注入的方式获取XXRedisTemplate模板对象；

```java

@RestController
public class RedisController {
    private final StringRedisTemplate stringRedisTemplate;
    private final StringRedisTemplate testStringRedisTemplate;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final RedisTemplate<Object, Object> testRedisTemplate;
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final ReactiveStringRedisTemplate testReactiveStringRedisTemplate;
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, Object> testReactiveRedisTemplate;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public RedisController(StringRedisTemplate stringRedisTemplate,
                           @Qualifier("test1StringRedisTemplate") StringRedisTemplate testStringRedisTemplate,
                           RedisTemplate<Object, Object> redisTemplate,
                           @Qualifier("test1RedisTemplate") RedisTemplate<Object, Object> testRedisTemplate,
                           ReactiveStringRedisTemplate reactiveStringRedisTemplate,
                           @Qualifier("test1ReactiveStringRedisTemplate") ReactiveStringRedisTemplate testReactiveStringRedisTemplate,
                           ReactiveRedisTemplate<String, Object> reactiveRedisTemplate,
                           @Qualifier("test1ReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> testReactiveRedisTemplate
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.testStringRedisTemplate = testStringRedisTemplate;
        this.redisTemplate = redisTemplate;
        this.testRedisTemplate = testRedisTemplate;
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.testReactiveStringRedisTemplate = testReactiveStringRedisTemplate;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.testReactiveRedisTemplate = testReactiveRedisTemplate;
    }

    @GetMapping("api/redis/test")
    public String test() {

        DataRedisFactory.getStringRedisTemplate().opsForValue().set("test", "你好", 100, TimeUnit.SECONDS);
        DataRedisFactory.getStringRedisTemplate("test1").opsForValue().set("test", "你好", 100, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set("test1", "你好1", 100, TimeUnit.SECONDS);
        testStringRedisTemplate.opsForValue().set("test2", "你好2", 100, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("test3", "test3", 100, TimeUnit.SECONDS);
        testRedisTemplate.opsForValue().set("test3", "test3", 100, TimeUnit.SECONDS);
        return DataRedisFactory.getStringRedisTemplate().opsForValue().get("test");
    }

    @GetMapping("api/redis/reactive")
    public String getInfo() {
        DataRedisFactory.getReactiveStringRedisTemplate().opsForValue().set("test-reactive", "reactive-你好", Duration.ofSeconds(100)).block();
        DataRedisFactory.getReactiveStringRedisTemplate("test1").opsForValue().set("test-reactive", "reactive-你好", Duration.ofSeconds(100)).block();
        reactiveStringRedisTemplate.opsForValue().set("test1-reactive", "你好1", Duration.ofSeconds(100)).block();
        testReactiveStringRedisTemplate.opsForValue().set("test1-reactive", "你好2", Duration.ofSeconds(100)).block();
        reactiveRedisTemplate.opsForValue().set("test3-reactive", "test3", Duration.ofSeconds(100)).block();
        testReactiveRedisTemplate.opsForValue().set("test4-reactive", "test4", Duration.ofSeconds(100)).block();
        reactiveRedisTemplate.opsForHash().put("test5-reactive","test5","test5").block();
        reactiveRedisTemplate.opsForHash().put("test5-reactive","test6","test6").block();
        return DataRedisFactory.getReactiveStringRedisTemplate().opsForValue().get("test-reactive").subscribe().toString();
    }
    }
```

