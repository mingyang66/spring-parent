##### 一、pom依赖

```xml
            <dependency>
                <groupId>io.github.mingyang66</groupId>
                <artifactId>emily-spring-boot-rateLimiter</artifactId>
                <version>${revision}</version>
            </dependency>
```

##### 二、属性配置

```properties
#限流组件开关
spring.emily.limiter.enabled=true
```

##### 三、注释

- RateLimiterOperation注解标记在方法、控制器上限值访问频率，value值可以通过%s占位符指定key的占位符，具体是由参数按照顺序来填充；
- DefaultRateLimiterMethodInterceptor默认拦截器有两个方法before、after需要具体的应用程序来实现，用来获取已经访问的次数、添加访问次数。

##### 四、案例

- DefaultRateLimiterMethodInterceptor拦截器方法实现

```java
@Component
public class CustomRateLimiterInterceptor extends DefaultRateLimiterMethodInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    public CustomRateLimiterInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public int before(String key) {
        String countStr = stringRedisTemplate.opsForValue().get(key);
        return countStr == null ? 0 : Integer.parseInt(countStr);
    }

    @Override
    public void after(String key, long timeout, TimeUnit timeunit) {
        stringRedisTemplate.opsForValue().increment(key);
        stringRedisTemplate.expire(key, timeout, timeunit);
    }
}

```

- @RateLimiterOperation注解标记在普通方法上

```java
    @Override
    @RateLimiterOperation(value = "SDK:limiter:%s:%s", timeout = 5, timeunit = TimeUnit.MINUTES, threshold = 3, message = "您已触发访问限制，请等待几分钟后再试。")
    public void rateLimiter(String key1, String key2) {
        System.out.println("---------------");
    }
```

- @RateLimiterOperation注解标记在控制器方法上

```java
    @RateLimiterOperation(value = "SDK:limiter:api:limit", timeout = 5, timeunit = TimeUnit.MINUTES, threshold = 3, message = "您已触发访问限制，请等待几分钟后再试。")
    @GetMapping("api/rate/limiter1")
    public void rate1() {
        System.out.println("接口限流");
    }
```

