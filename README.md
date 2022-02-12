# EmilyInfrustructure基础框架

> Oauth2相关代码请移步到feature_bak分支查看

### maven父pom和子pom的版本号批量修改

##### 1 设置新的版本号

```
./mvnw versions:set -DnewVersion=2.4.1
```

##### 2 撤销设置

```
./mvnw versions:revert
```

##### 3 提交设置

```
./mvnw versions:commit
```
##### 4.项目打包(同时处理项目所依赖的包)

```
mvn clean install -pl emily-spring-boot-starter -am
```
或
```
./mvnw clean install -pl emily-spring-boot-starter -am
```



| 参数 | 全程                   | 说明                                                         |
| ---- | ---------------------- | ------------------------------------------------------------ |
| -pl  | --projects             | 选项后可跟随{groupId}:{artifactId}或者所选模块的相对路径(多个模块以逗号分隔) |
| -am  | --also-make            | 表示同时处理选定模块所依赖的模块                             |
| -amd | --also-make-dependents | 表示同时处理依赖选定模块的模块                               |
| -N   | --non-                 | 表示不递归子模块                                             |
| -rf  | --resume-frm           | 表示从指定模块开始继续处理                                   |

### 打tag标签

##### 1.添加tag

```
git tag -a version1.0 -m 'first version'
```

##### 2.提交tag

```
git push origin --tags
```

其它tag操作参考：[tag操作指南](https://blog.csdn.net/Emily/article/details/78839295?ops_request_misc=%7B%22request%5Fid%22%3A%22158685673019724835840750%22%2C%22scm%22%3A%2220140713.130056874..%22%7D&request_id=158685673019724835840750&biz_id=0&utm_source=distribute.pc_search_result.none-task-blog-blog_SOOPENSEARCH-1)

------
#### 数据库多数据源组件

- 扩展点提供DataSourceCustomizer接口，AOP会根据拦截器的优先级判定使用优先级最高的拦截器
- 属性配置示例

```properties
#是否开启数据源组件, 默认：true
spring.emily.datasource.enabled=true
#是否拦截超类或者接口中的方法，默认：true
spring.emily.datasource.check-inherited=true
#默认数据源配置，默认：default
spring.emily.datasource.default-config=default
#驱动名称
spring.emily.datasource.config.default.driver-class-name=oracle.jdbc.OracleDriver
#配置url
spring.emily.datasource.config.default.url=jdbc:oracle:thin:@xx.xx.xx.xx:1521:qw
#用户名
spring.emily.datasource.config.default.username=root
#用户密码
spring.emily.datasource.config.default.password=123456
#数据库连接池类型
spring.emily.datasource.config.default.db-type=com.alibaba.druid.pool.DruidDataSource

spring.emily.datasource.config.slave.driver-class-name=oracle.jdbc.OracleDriver
spring.emily.datasource.config.slave.url=jdbc:oracle:thin:@x.x.x.x:we
spring.emily.datasource.config.slave.username=root
spring.emily.datasource.config.slave.password=123
spring.emily.datasource.config.slave.db-type=com.alibaba.druid.pool.DruidDataSource
```

#### Mybatis埋点组件

- 扩展点MybatisCustomizer，AOP根据拦截器的优先级判定使用优先级最高的拦截器
- 属性配置：

```properties
#是否开启mybatis拦截组件, 默认：true
spring.emily.mybatis.enabled=true
#是否拦截超类或者接口中的方法，默认：true
spring.emily.mybatis.check-class-inherited=true
```



#### Redis多数据源组件

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

#### logback日志组件

- 属性配置

```properties
#日志组件
#启动日志访问组件，默认：true
spring.emily.logback.enabled=true

#日志文件存放路径，默认是:./logs
spring.emily.logback.appender.path=./logs
#如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
spring.emily.logback.appender.append=true
#如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
spring.emily.logback.appender.prudent=false
#设置是否将输出流刷新，确保日志信息不丢失，默认：true
spring.emily.logback.appender.immediate-flush=true
#是否报告内部状态信息，默认；false
spring.emily.logback.appender.report-state=false

#是否开启基于文件大小和时间的SizeAndTimeBasedRollingPolicy归档策略
#默认是基于TimeBasedRollingPolicy的时间归档策略，默认false
spring.emily.logback.appender.rolling-policy.type=size_and_time_based
#设置要保留的最大存档文件数量，以异步方式删除旧文件,默认 7
spring.emily.logback.appender.rolling-policy.max-history=2
#最大日志文件大小 KB、MB、GB，默认:500MB
spring.emily.logback.appender.rolling-policy.max-file-size=10KB
#控制所有归档文件总大小 KB、MB、GB，默认:5GB
spring.emily.logback.appender.rolling-policy.total-size-cap=5GB
#设置重启服务后是否清除历史日志文件，默认：false
spring.emily.logback.appender.rolling-policy.clean-history-on-start=true

#是否开启异步记录Appender，默认：false
spring.emily.logback.appender.async.enabled=false
#队列的最大容量，默认为 256
spring.emily.logback.appender.async.queue-size=256
#默认，当队列还剩余 20% 的容量时，会丢弃级别为 TRACE, DEBUG 与 INFO 的日志，仅仅只保留 WARN 与 ERROR 级别的日志。想要保留所有的事件，可以设置为 0
spring.emily.logback.appender.async.discarding-threshold=0
# 根据所引用 appender 队列的深度以及延迟， AsyncAppender 可能会耗费长时间去刷新队列。
# 当 LoggerContext 被停止时， AsyncAppender stop 方法会等待工作线程指定的时间来完成。
# 使用 maxFlushTime 来指定最大的刷新时间，单位为毫秒。在指定时间内没有被处理完的事件将会被丢弃。这个属性的值的含义与 Thread.join(long)) 相同
# 默认是 1000毫秒
spring.emily.logback.appender.async.max-flush-time=1000
# 在队列满的时候 appender 会阻塞而不是丢弃信息。设置为 true，appender 不会阻塞你的应用而会将消息丢弃，默认为 false
spring.emily.logback.appender.async.never-block=false

#日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
spring.emily.logback.root.level=info
#通用日志输出格式，默认：[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n
spring.emily.logback.root.pattern=[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n
#基础日志文件路径,默认：""
spring.emily.logback.root.file-path=base
#是否将日志信息输出到控制台，默认：true
spring.emily.logback.root.console=true

#日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
spring.emily.logback.group.level=info
#模块日志输出格式，默认：%msg%n
spring.emily.logback.group.pattern=[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n
#是否将模块日志输出到控制台，默认：false
spring.emily.logback.group.console=true

#日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：DEBUG
spring.emily.logback.module.level=info
#模块日志输出格式，默认：%msg%n
spring.emily.logback.module.pattern=%msg%n
#是否将模块日志输出到控制台，默认：false
spring.emily.logback.module.console=true
```

#### RestTemplate请求组件

- 扩展点HttpClientCustomizer，AOP根据拦截器的优先级判定使用优先级最高者
- 属性配置

```properties
#Http RestTemplate组件开关，默认true
spring.emily.http-client.enabled=true
#Http RestTemplate拦截器开关，记录请求响应日志，默认true
spring.emily.http-client.interceptor=true
#http连接读取超时时间，默认5000毫秒
spring.emily.http-client.read-time-out=1000
#http连接连接超时时间，默认10000毫秒
spring.emily.http-client.connect-time-out=1000
```

#### Feign组件

- 扩展点FeignLoggerCustomizer，AOP根据拦截器的优先级判定使用优先级最高者

- 属性配置

```properties
spring.emily.feign.logger.enabled=true
```

#### API路由设置组件

```properties
#是否开启所有接口的前缀prefix,默认前面添加api
spring.emily.web.path.enable-all-prefix=true
#自定义添加前缀,默认api
spring.emily.web.path.prefix=api
#区分大小写,默认false
spring.emily.web.path.case-sensitive=false
#是否缓存匹配规则,默认null等于true
spring.emily.web.path.cache-patterns=true
#是否去除前后空格,默认false
spring.emily.web.path.trim-tokens=false
#设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
spring.emily.web.path.use-trailing-slash-match=true
#忽略URL前缀控制器设置,默认空
spring.emily.web.path.exclude=
```

#### API跨域组件

```properties
#开启跨域设置，默认false
spring.emily.web.cors.enable=false
#设置允许哪些源来访问,多个源用逗号分开
spring.emily.web.cors.allowed-origins=
#允许HTTP请求方法
spring.emily.web.cors.allowed-methods=GET,POST
#设置用户可以拿到的字段
spring.emily.web.cors.allowed-headers=
#设置浏览器是否应该发送凭据cookie
spring.emily.web.cors.allow-credentials=true
#设置响应HEAD,默认无任何设置，不可以使用*号
spring.emily.web.cors.exposed-headers=
#设置多长时间内不需要发送预检验请求，可以缓存该结果，默认1800秒
spring.emily.web.cors.max-age=1800
```

```


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

  

