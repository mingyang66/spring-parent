# 基础框架SDK

> master分支是基于java17及springboot 3.x.x的版本，原来基于java11及springboot2.7.x版本的代码在java11分支

- #### Maven’s Bill of Material (BOM)


```xml
    <dependencyManagement>
        <dependencies>
            <dependency>
            <dependency>
                <groupId>io.github.mingyang66</groupId>
                <artifactId>emily-dependencies</artifactId>
                <version>5.0.2</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

- 基于继承关系的Maven父依赖：


```xml
    <parent>
        <groupId>io.github.mingyang66</groupId>
        <artifactId>emily-spring-boot-parent</artifactId>
        <version>5.0.2</version>
        <relativePath/>
    </parent>
```

- [maven相关指令操作手册](https://github.com/mingyang66/spring-parent/blob/master/MAVEN.md)
- [git相关指令操作手册](https://github.com/mingyang66/spring-parent/blob/master/GIT.md)
- [oceansky-image图形验证码组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-project/oceansky-captcha)
- [emily-spring-boot-desensitize方法返回值和日志脱敏组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-desensitize)
- [emily-spring-boot-tracing全链路日志追踪组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-tracing)
- [emily-spring-boot-datasource数据库多数据源SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-datasource)
- [emily-spring-boot-logger日志组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-logger)
- [emily-spring-boot-transfer调用三方接口组件SDK使用手册](https://github.com/mingyang66/spring-parent/blob/master/emily-spring-boot-project/emily-spring-boot-transfer/README.md)
- [emily-spring-boot-i18n多语言翻译组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-i18n)


------

#### 

#### 三、Redis多数据源组件

- 属性配置

```properties
# 是否开启Redis多数据源配置，默认：true
spring.emily.redis.enabled=true
# Redis监控是否开启，默认：false
spring.emily.redis.monitor-enabled=false
# Redis监控固定间隔时间，默认：30s
spring.emily.redis.monitor-fire-rate=10s
# 是否开启连接校验，默认：false
spring.emily.redis.validate-connection=false
# 是否开启共享本地物理连接，默认：true
spring.emily.redis.share-native-connection=true

spring.emily.redis.config.default.client-type=lettuce
spring.emily.redis.config.default.database=15
spring.emily.redis.config.default.password=test12345
spring.emily.redis.config.default.sentinel.master=xxx
spring.emily.redis.config.default.sentinel.nodes=x.x.x.x:26380,x.x.x.x:26381,xx.x.x.x:26382
# 读取超时时间
spring.emily.redis.config.default.timeout=3000
# 连接超时时间
spring.emily.redis.config.default.connect-timeout=PT3S
spring.emily.redis.config.default.lettuce.pool.enabled=true
spring.emily.redis.config.default.lettuce.pool.max-active=8
spring.emily.redis.config.default.lettuce.pool.max-idle=8
#
spring.emily.redis.config.default.lettuce.pool.min-idle=4
spring.emily.redis.config.default.lettuce.pool.max-wait=-1
# 每隔多少时间空闲线程驱逐器关闭多余的空闲连接，且保持最少空闲连接可用，同时min-idle要大于0
spring.emily.redis.config.default.lettuce.pool.time-between-eviction-runs=PT0.1S

spring.emily.redis.config.test.client-type=lettuce
spring.emily.redis.config.test.database=15
spring.emily.redis.config.test.password=xx
spring.emily.redis.config.test.sentinel.master=xx
spring.emily.redis.config.test.sentinel.nodes=x.x.x.x:26379,x.x.x.x:26379,x.x.x.x:26379
spring.emily.redis.config.test.timeout=300
spring.emily.redis.config.test.lettuce.pool.max-active=8
spring.emily.redis.config.test.lettuce.pool.max-idle=8
spring.emily.redis.config.test.lettuce.pool.min-idle=0
spring.emily.redis.config.test.lettuce.pool.max-wait=-1
```



#### 七、API路由设置组件

```properties
#是否开钱添加前缀组件，默认：false
spring.emily.mvc.path.enabled=false
#自定义添加前缀,默认api
spring.emily.mvc.path.prefix=api
#区分大小写,默认false
spring.emily.mvc.path.case-sensitive=false
#是否缓存匹配规则,默认null等于true
spring.emily.mvc.path.cache-patterns=true
#是否去除前后空格,默认false
spring.emily.mvc.path.trim-tokens=false
#设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
spring.emily.mvc.path.use-trailing-slash-match=true
#忽略URL前缀控制器设置,默认空
spring.emily.mvc.path.exclude=
```

#### 八、API跨域组件

```properties
#开启跨域设置，默认：false
spring.emily.mvc.cors.enable=false
#设置允许哪些源来访问,多个源用逗号分开
spring.emily.mvc.cors.allowed-origins=
#允许HTTP请求方法
spring.emily.mvc.cors.allowed-methods=GET,POST
#设置用户可以拿到的字段
spring.emily.mvc.cors.allowed-headers=
#设置浏览器是否应该发送凭据cookie
spring.emily.mvc.cors.allow-credentials=true
#设置响应HEAD,默认无任何设置，不可以使用*号
spring.emily.mvc.cors.exposed-headers=
#设置多长时间内不需要发送预检验请求，可以缓存该结果，默认1800秒
spring.emily.mvc.cors.max-age=1800
```

#### 九、全局过滤器及灰度发布路由重定向组件

```properties
# 过滤器总开关，默认：true
spring.emily.filter.enabled=true
# 全局过滤器开关（解决读取请求参数后控制器拿不到参数问题），默认：true  
spring.emily.filter.global-switch=true
# 控制路由重定向开关，默认：false
spring.emily.filter.route-switch=false
```



#### 十二、返回值包装组件

- 支持所有数据类型返回值包装BaseResponse类；
- 支持通过注解@ApiResponseWrapperIgnore忽略掉返回值包装；
- 返回值类型为BaseResponse的忽略掉包装；
- 支持通过属性配置spring.emily.response.exclude=xx,xx配置模式忽略返回值包装（可以配置正则表达式）；
- 返回值类型是byte[]字节码流的忽略返回值包装；

组件SDK属性配置：

```properties
# 返回值包装SDK开关，默认：true
spring.emily.response.enabled=true
# 基于适配器模式的实现方案，默认：false
spring.emily.response.enabled-adapter=false
# 基于AOP切面的实现方案，默认：true
spring.emily.response.enabled-advice=true
# 排除指定url对返回值进行包装，支持正则表达式
spring.emily.response.exclude=abc/a.html

```

返回值包装案例：

```java
{
    "status": 0,
    "message": "SUCCESS",
    "data": {
        "username": "田晓霞",
        "password": "密码"
    },
    "spentTime": 3
}
```

consul服务查询、删除接口

- 查询服务接口Get

```java
http://127.0.0.1:8500/v1/agent/checks
```

- 删除consul服务接口PUT方法

```
http://127.0.0.1:8500/v1/agent/service/deregister/instance-id(实例ID)
```

##### IDEA快捷键

- 查询类的所有方法：

  ```
  F+command+F12
  ```

  

