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
##### 4.项目打包(同时处理项目所依赖的包)

```
mvn clean install -pl sgrain-spring-boot-starter -am
```
或
```
./mvnw clean install -pl sgrain-spring-boot-starter -am
```



| 参数 | 全程                   | 说明                                                         |
| ---- | ---------------------- | ------------------------------------------------------------ |
| -pl  | --projects             | 选项后可跟随{groupId}:{artifactId}或者所选模块的相对路径(多个模块以逗号分隔) |
| -am  | --also-make            | 表示同时处理选定模块所依赖的模块                             |
| -amd | --also-make-dependents | 表示同时处理依赖选定模块的模块                               |
| -N   | --non-                 | 表示不递归子模块                                             |
| -rf  | --resume-frm           | 表示从指定模块开始继续处理                                   |



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

- 配置关闭组件

```
spring.autoconfigure.exclude=xxx
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

- 配置关闭组件

```
spring.autoconfigure.exclude=xxx
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

- 配置关闭组件

```
spring.autoconfigure.exclude=xxx
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

- 配置关闭组件

```
spring.autoconfigure.exclude=xxx
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

- 配置关闭组件

```
spring.autoconfigure.exclude=xxx
```

- 支持两种验证方式

```
通过接口/token/generation接口获取token令牌，并且通过验证token的有效性来判断是否重复提交；
通过令牌和URL组合的方式作为主键创建分布式锁的模式，这种模式适合用户已经登录，存在用户token令牌的模式；
```