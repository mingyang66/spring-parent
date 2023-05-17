# EmilyInfrustructure基础框架

> Oauth2相关代码请移步到feature_bak分支查看

### maven父pom和子pom的版本号批量修改

#### 开源pom依赖引用

```xml
<!--Java图形验证码SDK-->
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-captcha</artifactId>
  <version>4.3.2</version>
</dependency>
<!--Java简繁体转换、多语言支持SDK-->  
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-language</artifactId>
  <version>4.3.2</version>
</dependency>
<!--Java实体类脱敏SDK-->  
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-sensitive</artifactId>
  <version>4.3.2</version>
</dependency>  
<!--Java基于jackson的序列化反序列化SDK-->  
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-json</artifactId>
  <version>4.3.2</version>
</dependency>  
<!--Java基于JWT获取签名解析签名SDK-->  
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-jwt</artifactId>
  <version>4.3.2</version>
</dependency>  
```



##### 1 设置新的版本号

```
./mvnw versions:set -DnewVersion=4.1.9
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

| 参数   | 全程                     | 说明                                                 |
|------|------------------------|----------------------------------------------------|
| -pl  | --projects             | 选项后可跟随{groupId}:{artifactId}或者所选模块的相对路径(多个模块以逗号分隔) |
| -am  | --also-make            | 表示同时处理选定模块所依赖的模块                                   |
| -amd | --also-make-dependents | 表示同时处理依赖选定模块的模块                                    |
| -N   | --non-                 | 表示不递归子模块                                           |
| -rf  | --resume-frm           | 表示从指定模块开始继续处理                                      |

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

#### 一、动态数据库多数据源组件

##### 特性：

- 支持指定默认数据源，如未标记使用哪个数据源则使用默认数据源；
- 支持动态切换不同的数据源；
- 支持扩展点能力，提供DataSourceCustomizer接口，AOP会根据拦截器的优先级判定使用优先级最高的拦截器；
- 提供@TargetDataSource注解切换不同的数据源；
- 可以通过配置更改默认数据源的标识；
- 支持从类、方法上读取切换不同数据源注解，方法上的注解标识优先级最高；
- 支持从父类、父接口及其方法上继承注解；
- druid数据库连接池支持的所有属性配置都支持；
- 支持宽松回退，即如果连接数据源事变，则回退到默认数据源；
- 属性配置示例

```properties
#https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8
#是否开启数据源组件, 默认：true
spring.emily.datasource.enabled=true
#是否拦截超类或者接口中的方法，默认：true
spring.emily.datasource.check-inherited=true
#默认数据源配置，默认：default
spring.emily.datasource.default-data-source=mysql
#这一项可配可不配，如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName
spring.emily.datasource.druid.mysql.driver-class-name=com.mysql.cj.jdbc.Driver
#连接数据库的url，不同数据库不一样
spring.emily.datasource.druid.mysql.url=jdbc:mysql://127.0.0.1:3306/sgrain?characterEncoding=utf-8&rewriteBatchedStatements=true
#连接数据库的用户名
spring.emily.datasource.druid.mysql.username=root
#用户密码（smallgrain）
spring.emily.datasource.druid.mysql.password=Lj9+VdH9qVvuG2t7/FBnvCtFBiLauk9uFlk2EWiRgG6IUFfbKKFX5Xxw7O0i2u8QPjB3hWYZEwEeYMw/Yq89Zg==
#数据库连接池类型
spring.emily.datasource.druid.mysql.db-type=mysql

#初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时，默认：0
spring.emily.datasource.druid.mysql.initial-size=2
#最小连接池数，默认：0
spring.emily.datasource.druid.mysql.min-idle=2
#最大连接数，默认：8
spring.emily.datasource.druid.mysql.max-active=8
#获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁，默认：-1（推荐内网800ms,外网1200ms,因为tcp建立连接重试一般需要1s）
spring.emily.datasource.druid.mysql.max-wait=-1

#mysql默认使用ping模式,可以通过设置系统属性System.getProperties().setProperty("druid.mysql.usePingMethod", "false")更改为sql模式
#用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。默认：缺省
# mysql默认：SELECT 1  oracle默认：SELECT 'x' FROM DUAL
spring.emily.datasource.druid.mysql.validation-query=SELECT 1
#单位：秒，检测连接是否有效的超时时间。底层调用jdbc Statement对象的void setQueryTimeout(int seconds)方法，默认：-1
spring.emily.datasource.druid.mysql.validation-query-timeout=-1
#申请连接时执行validationQuery检测连接是否有效，这个配置会降低性能。默认：false (如果test-on-borrow为true,那么test-while-idle无效)
spring.emily.datasource.druid.mysql.test-on-borrow=false
#建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。默认：true
spring.emily.datasource.druid.mysql.test-while-idle=true
#归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。默认：false
spring.emily.datasource.druid.mysql.test-on-return=false

#是否对空闲连接进行保活，默认：false
spring.emily.datasource.druid.mysql.keep-alive=true
#触发心跳的间隔时间（DestroyTask守护线程检测连接的间隔时间），默认：60*1000 一分钟
spring.emily.datasource.druid.mysql.time-between-eviction-runs-millis=60000
#连接保持空闲而不被驱逐的最小时间（保活心跳只会对存活时间超过这个值的连接进行），默认：1000L * 60L * 30L
spring.emily.datasource.druid.mysql.min-evictable-idle-time-millis=1800000
#连接保持空闲最长时间（连接有任何操作，计时器重置，否则被驱逐），默认：1000L * 60L * 60L * 7
spring.emily.datasource.druid.mysql.max-evictable-idle-time-millis=25200000
#保活检查间隔时间，默认：60*1000*2毫秒，要求大于等于2分钟（要大于min-evictable-idle-time-millis）
spring.emily.datasource.druid.mysql.keep-alive-between-time-millis=120000

#https://github.com/alibaba/druid/wiki/%E8%BF%9E%E6%8E%A5%E6%B3%84%E6%BC%8F%E7%9B%91%E6%B5%8B
#连接池泄漏监测，当程序存在缺陷时，申请的连接忘记关闭，这时就存在连接泄漏了，开启后对性能有影响，建议生产关闭，默认：false
spring.emily.datasource.druid.mysql.remove-abandoned=true
#默认：300*1000
spring.emily.datasource.druid.mysql.remove-abandoned-timeout-millis=300000
#回收连接时打印日志，默认：false
spring.emily.datasource.druid.mysql.log-abandoned=true

#物理超时时间，默认：-1
spring.emily.datasource.druid.mysql.phy-timeout-millis=-1
#物理最大连接数，默认：-1（不建议配置）
spring.emily.datasource.druid.mysql.phy-max-use-count=-1

#是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。默认：false
spring.emily.datasource.druid.mysql.pool-prepared-statements=true
#要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100，默认：10
#默认：10，如果不设置此属性大于0，则PS缓存默认关闭
spring.emily.datasource.druid.mysql.max-pool-prepared-statement-per-connection-size=10

#数据库连接失败重试几次之后允许终止或休眠一段时间再重试，默认：1
spring.emily.datasource.druid.oracle.connection-error-retry-attempts=1
#数据库连接失败，是否退出重试，默认：false
spring.emily.datasource.druid.oracle.break-after-acquire-failure=false
#数据库连接失败，重试间隔多久，默认：500ms
spring.emily.datasource.druid.oracle.time-between-connect-error-millis=500

#是否开启StatViewServlet，默认：false
spring.datasource.druid.stat-view-servlet.enabled=true
#用户名
spring.datasource.druid.stat-view-servlet.login-username=admin
#密码
spring.datasource.druid.stat-view-servlet.login-password=admin
#访问URL
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
#允许清空统计数据
spring.datasource.druid.stat-view-servlet.reset-enable=true
#拒绝访问的IP地址，多个用逗号分隔
spring.datasource.druid.stat-view-servlet.deny=
#允许访问的IP地址，多个用逗号分隔
spring.datasource.druid.stat-view-servlet.allow=127.0.0.1

#是否开启WebStatFilter，默认：false
spring.datasource.druid.web-stat-filter.enabled=true
#URI监控，排除指定的请求，默认：*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*
spring.datasource.druid.web-stat-filter.exclusions=*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*
#URI监控，指定监控URI路径，默认：/*
spring.datasource.druid.web-stat-filter.url-pattern=/*
#Session监控，是否开启，默认：null
spring.datasource.druid.web-stat-filter.session-stat-enable=true
#Session监控session数量，默认：null
spring.datasource.druid.web-stat-filter.session-stat-max-count=1000
#URI监控，是否开启单个URI调用链路，默认：false
spring.datasource.druid.web-stat-filter.profile-enable=true
#如何使用待探究
spring.datasource.druid.web-stat-filter.principal-cookie-name=admin.user
spring.datasource.druid.web-stat-filter.principal-session-name=admin.user
#Spring监控代码包路径，支持多个包，用逗号隔开
spring.datasource.druid.aop-patterns=com.emily.infrastructure.test.*

#开启指定的过滤器
spring.emily.datasource.druid.mysql.filters=stat,wall,log4j2,config
#druid.stat.mergeSql是否合并sql，默认：false
#druid.stat.slowSqlMillis慢sql查询阀值，默认：3秒
#druid.stat.logSlowSql是否开启慢sql打印，默认：false
#druid.stat.slowSqlLogLevel配置展示sql的日志级别,默认：ERROR
#config.decrypt是否开启密码秘钥解密，默认：false
#config.decrypt.key密码解密公钥
spring.emily.datasource.druid.mysql.connection-properties=\
  druid.stat.mergeSql=true;\
  druid.stat.slowSqlMillis=2;\
  druid.stat.logSlowSql=true;\
  druid.stat.slowSqlLogLevel=error;\
  config.decrypt=true;\
  config.decrypt.key=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKxbGuex8VbSRFn99y8xPkE+3uUzKFgQSl06RwEl+MC1o/Ghy9XxGs5y1jbOFwybi/OtyH52Cm8Ciq2USMzw8DUCAwEAAQ==
#密码解密回调类
spring.emily.datasource.druid.mysql.password-callback-class-name=com.alibaba.druid.util.DruidPasswordCallback

```

#### 二、Mybatis埋点组件

- 扩展点MybatisCustomizer，AOP根据拦截器的优先级判定使用优先级最高的拦截器
- 属性配置：

```properties
#是否开启mybatis拦截组件, 默认：true
spring.emily.mybatis.enabled=true
#是否拦截超类或者接口中的方法，默认：true
spring.emily.mybatis.check-class-inherited=true
```

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

#### 四、logback日志组件

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

#### 五、RestTemplate请求组件

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

- 提供@TargetHttpTimeout注解设置单个Http请求读取、连接超时时间，示例程序如下：

```java
@RequestMapping("api/http")
@RestController
public class HttpClientController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("get1")
    public BaseResponse get1(HttpServletRequest request) {
        String timeout = request.getParameter("timeout");
        BaseResponse<String> result;
        try {
            HttpContextHolder.bind(RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(-1).build());
            result = restTemplate.getForObject("http://127.0.0.1:8080/api/http/testResponse?timeout=" + timeout, BaseResponse.class);
        } finally {
            HttpContextHolder.unbind();
        }
        return result;
    }

    @GetMapping("get2")
    @TargetHttpTimeout(readTimeout = 2000)
    public BaseResponse get2(HttpServletRequest request) {
        String timeout = request.getParameter("timeout");
        BaseResponse<String> result = restTemplate.getForObject("http://127.0.0.1:8080/api/http/testResponse?timeout=" + timeout, BaseResponse.class);

        return result;
    }


    @GetMapping("testResponse")
    public String testResponse(HttpServletRequest request) throws IllegalArgumentException {
        String timeout = request.getParameter("timeout");
        try {
            Thread.sleep(NumberUtils.toLong(timeout, 0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "你好";
    }

    @Autowired
    private TestTimeout testTimeout;

    @PostConstruct
    public void init() {
        //获取环境变量，初始化服务器端IP
        ScheduledExecutorService service = TtlExecutors.getTtlScheduledExecutorService(Executors.newScheduledThreadPool(2));
        service.scheduleAtFixedRate(() -> {
            try {
                testTimeout.loadStr();
            } catch (Exception e) {
            }

        }, 5, 5, TimeUnit.SECONDS);
    }


}

```

TestTimeout类代码：

```java
@Service
public class TestTimeout {
    @Autowired
    private RestTemplate restTemplate;

    @TargetHttpTimeout(readTimeout = 4000)
    public String loadStr() {
        BaseResponse<String> result = restTemplate.getForObject("http://127.0.0.1:8080/api/http/testResponse?timeout=3000", BaseResponse.class);
        System.out.println(result.getData());
        return result.getData();
    }
}
```

#### 六、Feign组件

- 扩展点FeignLoggerCustomizer，AOP根据拦截器的优先级判定使用优先级最高者

- 属性配置

```properties
spring.emily.feign.logger.enabled=true
```

- 支持全局设置超时时间及单个FeignClient设置超时时间

```properties
# Feign Clients contextId 默认配置名
feign.client.default-config=default
# 读取超时时间, 默认：60*1000 毫秒
feign.client.config.default.read-timeout=5000
# 请求超时时间，默认：10*1000 毫秒
feign.client.config.default.connect-timeout=10000
# 自定义读取超时时间
feign.client.config.custom.read-timeout=2000
# 自定义连接超时时间
feign.client.config.custom.connect-timeout=3000
```

- 默认全局超时FeignClient使用示例：

```java
@FeignClient(value = "connect", url = "http://127.0.0.1:9000/api/feign")
public interface DefaultFeignHandler {
    /**
     * 默认超时请求
     */
    @GetMapping("connect")
    BaseResponse<String> getConnect(@RequestParam("timeout") int timeout);
}
```

- 自定义超时时间使用示例：

```java
@FeignClient(value = "custom", url = "http://127.0.0.1:9000/api/feign", contextId = "custom")
public interface CustomFeignHandler {
    /**
     * 自定义超时请求
     */
    @GetMapping("custom")
    BaseResponse<String> getCustom(@RequestParam("timeout")  int timeout);
}
```

#### 七、API路由设置组件

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

#### 八、API跨域组件

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

#### 九、全局过滤器及灰度发布路由重定向组件

```properties
# 过滤器总开关，默认：true
spring.emily.filter.enabled=true
# 全局过滤器开关（解决读取请求参数后控制器拿不到参数问题），默认：true  
spring.emily.filter.global-switch=true
# 控制路由重定向开关，默认：false
spring.emily.filter.route-switch=false
```

#### 十、实体类数据脱敏组件

- @JsonSensitive注解标注在实体类上；
- @JsonSimField注解标注在字符串属性上，可以指定隐藏的类型；
- @JsonFlexField注解标注在复杂脱敏数据类型上；
- 支持对父类的属性进行脱敏；

```java
@JsonSensitive
public class JsonRequest extends Animal{
    @JsonFlexField(fieldKeys = {"email", "phone"}, fieldValue = "fieldValue", types = {SensitiveType.EMAIL, SensitiveType.PHONE})
    private String fieldKey;
    private String fieldValue;
    @JsonFlexField(fieldKeys = {"email", "phone"}, fieldValue = "fieldValue1")
    private String fieldKey1;
    private String fieldValue1;
    @NotEmpty
    @JsonSimField(SensitiveType.USERNAME)
    private String username;
    @JsonSimField
    private String password;
    @JsonSimField(SensitiveType.EMAIL)
    private String email;
    @JsonSimField(SensitiveType.ID_CARD)
    private String idCard;
    @JsonSimField(SensitiveType.BANK_CARD)
    private String bankCard;
    @JsonSimField(SensitiveType.PHONE)
    private String phone;
    @JsonSimField(SensitiveType.PHONE)
    private String mobile;
    }
```

#### 十一、I18n多语言支持组件

- @ApiI18n注解标注在实体类上；
- @ApiI18nProperty注解标注在实体类字符串属性上；
- 支持对父类属性进行多语言支持；

```java
@ApiI18n
public class Student extends People {
    @ApiI18nProperty
    private String name;
    @ApiI18nProperty
    private int age;
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

  

