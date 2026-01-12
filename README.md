# 基础框架SDK

> master分支是基于java21及springboot 4.0.1的版本，原来基于java11及springboot2.7.x版本的代码在java11分支

- #### Maven’s Bill of Material (BOM)


```xml
    <dependencyManagement>
        <dependencies>
            <dependency>
            <dependency>
                <groupId>io.github.mingyang66</groupId>
                <artifactId>emily-dependencies</artifactId>
                <version>6.0.0</version>
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
        <version>6.0.0</version>
        <relativePath/>
    </parent>
```

- [maven相关指令操作手册](https://github.com/mingyang66/spring-parent/blob/master/MAVEN.md)
- [git相关指令操作手册](https://github.com/mingyang66/spring-parent/blob/master/GIT.md)
- [emily-captcha图形验证码组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-project/emily-captcha/README.md)
- [emily-spring-boot-desensitize方法返回值和日志脱敏组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-desensitize/README.md)
- [emily-spring-boot-tracing全链路日志追踪组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-tracing/README.md)
- [emily-spring-boot-datasource数据库多数据源SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-datasource/README.md)
- [emily-spring-boot-logger日志组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-logger/README.md)
- [emily-spring-boot-transfer调用三方接口组件SDK使用手册](https://github.com/mingyang66/spring-parent/blob/master/emily-spring-boot-project/emily-spring-boot-transfer/README.md)
- [emily-spring-boot-i18n多语言翻译组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-i18n/README.md)
- [emily-spring-boot-rateLimiter限流组件SDK使用手册](https://github.com/mingyang66/spring-parent/blob/master/emily-spring-boot-project/emily-spring-boot-rateLimiter/README.md)
- [emily-spring-boot-security加解密组件SDK使用手册](https://github.com/mingyang66/spring-parent/blob/master/emily-spring-boot-project/emily-spring-boot-security/README.md)
- [emily-spring-boot-redis组件SDK使用手册](https://github.com/mingyang66/spring-parent/blob/master/emily-spring-boot-project/emily-spring-boot-redis/README.md)
- [emily-spring-boot-rabbitmq组件SDK使用手册](https://github.com/mingyang66/spring-parent/blob/master/emily-spring-boot-project/emily-spring-boot-rabbitmq/README.md)


![架构图](https://github.com/mingyang66/spring-parent/blob/master/framework.png)


------

####


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

  

