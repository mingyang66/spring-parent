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

- [oceansky-image图形验证码组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-project/oceansky-captcha)
- [emily-spring-boot-desensitize方法返回值和日志脱敏组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-desensitize)
- [emily-spring-boot-tracing全链路日志追踪组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-tracing)
- [emily-spring-boot-datasource数据库多数据源SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-datasource)
- [emily-spring-boot-logger日志组件SDK使用手册](https://github.com/mingyang66/spring-parent/tree/master/emily-spring-boot-project/emily-spring-boot-logger)


### maven父pom和子pom的版本号批量修改

##### 1 设置新的版本号

```
./mvnw versions:set -DnewVersion=4.4.8
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

3.如果文件已被Git跟踪（已经在版本控制中），则需要使用如下命令，然后在.gitignore文件中添加

```sh
git rm --cached 文件路径
```



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

  

