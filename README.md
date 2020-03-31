# spring-parent
### maven父pom和子pom的版本号批量修改

##### 1 设置新的版本号

```
mvn versions:set -DnewVersion=2.1.1.RELEASE
```

##### 2 撤销设置

```
mvn versions:revert
```

##### 3 提交设置

```
mvn versions:commit
```
------
### 自动化配置组件AutoConfiguration
##### 1.拦截控制器日志组件

- 配置关闭组件

```
spring.sgrain.log-aop.enable=true
```

- 注解关闭组件

```
@SpringBootApplication(exclude = {LogAopAutoConfiguration.class})
```

##### 2.RestTemplate网络请求配置组件

- 配置关闭组件

```
spring.sgrain.http-client.enable=true
```

- 注解关闭组件

```
@SpringBootApplication(exclude = {HttpClientAutoConfiguration.class})
```

##### 3.Redis配置组件

- 配置关闭组件

```
spring.sgrain.redis.enable=true
```

- 注解关闭组件

```
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
```

##### 4.控制器返回值包装组件

- 配置关闭组件

```
spring.sgrain.return-value.enable=true
```

- 注解关闭组件

```
@SpringBootApplication(exclude = {ReturnValueAutoConfiguration.class})
```

##### 5.限流组件

- 配置关闭组件

```
spring.sgrain.rate-limit.enable=true
```

- 注解关闭组件

```
@SpringBootApplication(exclude = {RateLimitAutoConfiguration.class})
```

##### 6.幂等性组件（防止接口重复提交）

- 配置关闭组件

```
spring.sgrain.idempotent.enable=true
```

- 注解关闭组件

```
@SpringBootApplication(exclude = {IdempotentAutoConfiguration.class})
```

- 支持两种验证方式

```
通过接口/token/generation接口获取token令牌，并且通过验证token的有效性来判断是否重复提交；
通过令牌和URL组合的方式作为主键创建分布式锁的模式，这种模式适合用户已经登录，存在用户token令牌的模式；
```