###
-----
基础库发布4.3.3
Bug Fixes
1. 新增oceansky-date SDK组件，新增DateUtils工具类，内部包含14个工具方法，涉及字符串转日期，日期转字符串；
2. 新增DateConvertUtils日期相互转换工具类；
3. 新增DateCompareUtils日期比大小工具类；
4. 新增DateComputeUtils日期计算相关工具类；
5. 
-----
基础库发布4.3.2
Bug Fixes
1. 去除jaxb-api的pom引用，删除Md5Utils工具类；
2. 单元测试junit-test依赖应用更改为dependencyManagement;
3. 异常捕获多语言支持，并对多语言组件做优化调整；
4. pom.xml文件新增url标签指定github地址；
5. DateUtils工具类新增两个toDate方法，将LocalDateTime、LocalDate转换为Date对象；
6. DateUtils工具类新增三个方法toLocalDateTime、toLocalDate、toLocalTime;
7. DateUtils工具类新增parseLocalDateTime、parseLocalDate、parseLocalTime三个将字符串转为日期对象方法；
8. 新增oceansky-jwt SDK模块，支持自动生成RSA公私钥，天剑获取公钥私钥方法，支持JWT使用RSA公钥和私钥加密；

-----
基础库发布4.3.1
Bug Fixes

1. springboot升级到2.7.11版本；
2. 添加BaseResponse的建造器BaseResponseBuilder，替换SDK中所有基于BaseResponse构建对象方法；
3. 添加BaseLogger的建造器BaseLoggerBuilder类，替换SDK中所有基于BaseLogger构建对象的方法；
4. 新增图形验证码组件，核心工具类CaptchaUtils、Captcha、CaptchaBuilder、CaptchaType;
5. DateUtils工具类重构，DateFormatType枚举类重命名为DatePatternType;
6. 新增简繁体组件oceansky-language SDK;
7. 将emily-spring-boot-common组件更改为oceansky-commons,变更为完全纯java版本；
8. 新增oceansky-json组件SDK;
9. 抽离commons包中的简繁体翻译SDK;
10. 抽离出oceansky-sensitive脱敏SDK;
11. 抽离出oceansky-captcha图形验证码SDK;

-----
基础库发布4.2.0
Bug Fixes

1. I18n多语言支持翻译当前类中父类的属性;
2. 脱敏工具类SensitiveUtils支持父类中的属性脱敏；
3. I18n多语言翻译支持Collection、Map、Array存储的值为字符串；
4. 重构SDK脱敏组件，调整如下：
   一、添加JavaBeanUtils工具类，抽离SensitiveUtils工具类中的公共方法isFinal、isModifierFinal、checkModifierNativeSyncStrict、checkModifierFinalStaticTransVol并优化；
   二、重构SensitiveUtils工具类，降低代码复杂度；
   三、支持对当前类的所有父类中的属性进行脱敏(之前不支持);
   四、支持对属性类型为Collection<String>、Map<String, String>、Array<String>集合类型中的数据进行脱敏处理（之前不支持）；
   五、支持对注解@JsonFlexField标注的复杂类型多层嵌套脱敏（之前不支持）;
   六、@JsonSensitive注解属性include过期作废，是否解析嵌套类的属性判定依据为类上标注的@JsonSensitive;
5. 新增实体类脱敏工具类DeSensitiveUtils，支持所有的嵌套类、属性、集合、父类属性脱敏，并且返回原对象；
6. 日志记录支持对参数类型是String类型的脱敏处理；

-----
基础库发布4.1.9
Bug Fixes

1. 删除无用的openfeign引用；
2. 删除RPC相关三个模块及依赖引用；
3. 删除log4j多余模块；
4. Java日期对象及JDBC日期对象相互转换组件验证及新增文档；
5. RestTemplate请求记录请求header，优化RequestHelper帮助类；
6. druid升级到1.2.16，小版本修复一些已知问题；
7. springboot升级到2.7.10版本；
8. 新增NumberInfo数字常量类；
9. 重命名LanguageCache为LanguageMap;
10. 新增基于注解的I18n多语言支持工具类I18nUtils、ApiI18n和ApiI18nProperty注解；
11. 优化重构脱敏工具类SensitiveUtils及其相关注解；

-----
基础库发布4.1.8
Bug Fixes

1.
DateUtils工具类新增getRemainTimeOfDay、getRemainDayOfMonth、getRemainDayOfYear方法，并将所有的BasicException异常修改为IllegalArgumentException;
2. springboot升级到2.7.8；
3. 新增重新定义RequestMappingHandlerMapping路由映射组件，支持用户自定义扩展路由到指定的控制器；
4. 优化全局过滤器自动化配置类；
5. 添加控制器路由重定向组件；
6. mybatis-spring-boot-starter升级到2.3.0；

-----
基础库发布4.1.7
Bug Fixes

1. 新增JsonFlexField注解，脱敏复杂数据类型；
2. 解决实体类外层包装是BaseResponse无法脱敏问题；
3. 实体类中字段是数组类型脱敏处理增强优化；
4. 脱敏实现类方法优化调整；
5. 耗时字段由time更改为spentTime;
6. AppHttpStatus、DateFormat枚举类重命名；
7. 脱敏工具类对数组类型是8中基本类型做兼容；
8. 全局异常捕获新增IllegalArgumentException、ValidationException两个异常类；
9. 脱敏注解重命名为JsonSimField、JsonSensitive;
10. RabbitMQ消息中间件支持默认连接容器，可以不用配置containerFactory;
11. RabbitMQ消息中间件SDK支持多个集群地址配置；
12. RabbitMQ消息中间件将不同的bean注入到容器之中优化；
13. Druid升级到1.2.15；
14. springboot版本升级到2.7.6；
15. 解决ThreadContextHolder header如果为空串时导致不可以生成事物流水号问题；
16. DateUtils工具类新增获取指定日期剩余时间方法getRemainOfDay；
17. FeignLogLoadBalancerLifecycle类新增判空，防止吞并真实的异常堆栈信息;

-----
基础库发布4.1.6
Bug Fixes

1. springboot升级到2.7.5；
2. mysql依赖包引用修改；
3. ContextHolder更名为ThreadContextHolder；
4. 线程上线文相关类的peek方法重命名为current;
5. API、Mapper、Feign支持@JsonSensitive脱敏注解；
6. 脱敏工具类值支持数组集合由List更改为Collection;
7. 脱敏工具类支持数组类型，如：Object[];
8. 脱敏工具类支持BigDecimal类型转换，上一版本不支持；
9. 脱敏工具类支持各种复杂的类型转换，实例如下（Persion为实体类型）：

- List<Persion>
- List<Persion[]>
- List<Map<String, Persion>>
- List<Map<String, Persion[]>>
- List<Map<String, List<Persion>>>
- List<Map<String, List<Persion[]>>>
- Map<String, Persion>
- Map<String, List<Persion>>
- Map<String, List<Persion[]>>
- Map<String,Map<String, Persion>>
- Set集合相关组合
- 支持所有Collection、Map、数组各种变体类型
- 其中Persion实体类内部支持以上各种复杂集合组合类型；

10. 新增注解@JsonSerialize，只能标注在实体类上，只有标注了此注解脱敏注解@JsonSensitive才会生效，默认只脱敏实体类一层，如需脱敏嵌套层则将include属性设置为true;
    使用示例如下：

> ```java
> @JsonSerialize(include = false)
> public class JsonResponse {
>     @JsonSensitive(SensitiveType.USERNAME)
>     private String username;
> ```

11. 脱敏工具类中将实体类中的枚举类型标记为最终类型，减少冗余处理逻辑；
12. 脱敏工具类中的枚举类标记为最终类的BigDecimal类型更改为Number类型，扩展兼容BigDecimal、BigInteger、AtomicInteger、AtomicLong等等；

-----
基础库发布4.1.5
Bug Fixes

1. 日志记录支持header中传递appType、appVersion；
2. 优化阶段标识及ContextHolder类；
3. DelegateRequestWrapper类引入IOUtils类优化；
4. protobuf版本升级到3.21.5；
5. Druid 1.2.12版本发布，连接池DruidDataSource支持新的配置connectTimeout和socketTimeout，分别都是10秒。这个默认值会减少因为网络丢包时导致的连接池无法创建链接;
6. spirngboot升级到2.7.4版本；
7. Druid 1.2.13版本发布，修复1.2.12版本引入connectTimeout、socketTimeout导致的BUG;
8. TTL升级到2.14.1版本；
9. 全局异常捕获BindException异常展示具体的异常信息；
10. commons-collections4包依赖引用删除；
11. Druid 1.2.14版本升级；
12. TTL升级到2.14.2版本；

-----
基础库发布4.1.4
Bug Fixes

1. 解决请求入参为非法参数是事务流水号跟上游服务不一致问题；
2. 修改httpclient依赖的版本为跟spring-boot-starter-parent依赖；
3. 修改commons-lang3依赖版本为跟spring-boot-starter-parent依赖；
4. 修改netty-all依赖版本为跟spring-boot-starter-parent依赖；
5. 修改jaxb-api依赖版本为跟spring-boot-starter-parent依赖；
6. 新增支持多语言的枚举类LanguageType；
7. 新增多语言支持缓存类LanguageCache;
8. HeaderInfo类中新增language常量；
9. 新增获取Redis key的帮助方法com.emily.infrastructure.redis.helper.RedisDbHelper.getKey；
10. springboot升级到2.7.3版本；
11. transmittable-thread-local版本升级2.14.0；
12. 对ContextHolder.unbind解除全局上下文绑定关系优化，解决OOM异常问题；

-----
基础库发布4.1.3
Bug Fixes

1. RequestHelper.getObjectMap方法名重命名为objectToMap；
2. 事务流水号统一为UUIDUtils工具类生成；
3. 新增全局异常捕获UnknownContentTypeException、ResourceAccessException；
4. 新增@TargetHttpTimeout注解及其支持的相关拦截器切面控制设置单个RestTemplate请求超时时间；
5. RestTemplate基础组件优化调整；
6. FeignClient设置全局和单个超时时间示例代码开发；
7. 删除无用的feign-httpclient依赖引用；

-----
基础库发布4.1.2
Bug Fixes

1. 全局异常捕获组件抛出异常时添加状态码，即添加@ResponseBody、@ResponseStatus注解标注
2. 对请求参数日志记录为数组类型的做兼容，如下：

```java
    @PostMapping("postList")
public int postList(@RequestBody List<User> list){
        System.out.println(JSONUtils.toJSONPrettyString(list));
        return 0;
        }

@PostMapping("postArray")
public int postList(@RequestBody User[]list){
        System.out.println(JSONUtils.toJSONPrettyString(list));
        return 0;
        }
```

3. 新增获取实际参数名工具类ParamNameUtils；
4. 优化DataSourceContextHolder类中set-bind,remove-unbind;
5. 优化ContextHolder类中set-bind,reomve-unbind，get-peek;
6. 修改DataSourceContextHolder的get方法名为peek;
7. 优化隐藏参数方法RequestHelper.getMethodArgs；
8. 新增DataSourceHelper帮助类，优化属性配置类，保持单一职责原理；
9. 新增ArithmeticException非法计算异常捕获；
10. 新增事务包装注解@TargetTransactional；
11. 新增脱敏注解@Sensitive支持；
12. springboot升级到2.7.2;
13. 优化Feign调用日志记录及上下文；
14. 接口拦截器新增对404 Not Fund 接口异常处理；
15. RequestHelper新增Object转Map方法；

-----
基础库发布4.1.1
Bug Fixes

1. ApplicationStatus异常状态码枚举优化调整，并更改使用异常的相关类
2. TTL版本更新到2.13.0
3. Druid版本更新到1.2.11
4. 数据库多数据源组件获取数据库属性配置优化调整
5. 新增tomcat开启http端口组件
6. RabbitMQ消息中间件组件支持同时连接同一个MQ集群中不同的Virtual-Host;
7. TTL升级到2.13.1，添加@Contract注解，精细化控制输入的null值warning;
8. springboot升级到2.7.1
9. TTL升级到2.13.2版本；
10. 全局异常捕获组件重新定义，抽离出GlobalExceptionCustomizer父接口，并定义默认实现DefaultGlobalExceptionHandler，从而可以支持自定义扩展异常处理，完全自定义异常处理；

-----
基础库发布4.1.0
Bug Fixes

1. springboot升级到2.6.6
2. springboot升级到2.7.0
3. 数据库多数据源组件AutoConfiguration配置方式由spring.factories更改为最新META-INF/spring/{全限定名}.imports模式
4. 优化rabbitmq模块及移动文档及依赖
5. 优化LRU缓存
6. ContextHolder涉及clientIp和serverIp优化调整
7. 动态数据库多数据源组件默认数据库优化（原先默认数据库标识如果不更改无问题，如果更改还采用默认标识模式，则会出现切换异常）
8. Redis多数据源组件默认数据库优化调整。
9. 新增RabbitMQ消息中间件多配置SDK。
10. druid-spring-boot-starter升级到1.2.9
11. 阶段标识优化解决重复记录日志问题
12. 优化动态数据源命名

-----
基础库发布4.0.9
Bug Fixes

1. 优化异常统一处理
2. 控制器请求在url转换阶段所报的异常记录到日志平台；
3. 数据库多数据源组件去除getDataSource方法，优化默认拦截器，支持两种扩展点方案；
4. transmittable-thread-local版本升级到2.12.6
5. IOCContext新增getBeansOfType方法支持获取一个类的所有实例对象
6. 动态数据库多数据源组件新增resolveSpecifiedLookupKey方法，优化初始化动态切换实例对象；
7. 动态数据库多数据源组件新增resolveSpecifiedLookupKey方法根据不同的条件切换数据源；
8. 动态多数据源属性配置优化调整，新增默认数据源配置判定

-----
基础库发布4.0.8
Bug Fixes

1. 扩展点默认拦截器命名前加Default
2. 优化Feign扩展点
3. Mybatis方法请求参数优化调整，提取到公共获取参数类RequestHelper中；
4.

com.emily.infrastructure.core.config.properties.PropertyService,com.emily.infrastructure.core.config.ConfEnvironmentPostProcessor,
com.emily.infrastructure.core.config.EmilyOriginTrackedPropertiesLoader,com.emily.infrastructure.core.config.EmilyPropertiesPropertySourceLoader
四个类删除；

5. 上下文配置类名称变更，TraceContextHolder名称变更为ContextHolder;
6. IOCContext类优化实现ApplicationContextInitializer初始化接口，删除自定义初始化类，调整IOCContext类包路径
7. druid多数据源属性配置原理分析，添加多数据源守护线程
8. 升级springboot到2.6.4版本
9. 新增com.emily.infrastructure.common.utils.StrUtils.replaceChar字符串工具方法
10. 优化ContextHolder移除当前线程持有的变量方法
11. 优化数据库中间件，新增扩展点before、after方法
12. 新增LRUCache类
13. 动态数据库多数据源中间件支持Hikari数据库连接池，无需任何代码层面调整就可以自动切换使用不同的数据库连接池

-----
基础库发布4.0.7
Bug Fixes

1. 优化数据源注解@TargetDataSource注解说明
2. 升级springboot到2.6.3版本
3. netty升级到4.1.73.Final
4. 删除配置类EmilyBeanFactoryPostProcessorAutoConfiguration
5. Feign拦截日志自动化配置废弃掉AspectJ表达式方式，使用AnnotationMatchingPointcut类实现
6. 自动化配置中 @ConditionalOnBean(xx.class)注解使用去除，解决不可加载问题
7. 全链路日志追踪在多线程池化时会重新创建事务编号问题解决
8. 日志组件新增初始化类LogbackApplicationContextInitializer类，在项目启动时即初始化日志组件，即程序启动时最先加载日志组件，防止系统日志打印丢失
9. 多数据源统一拦截器父接口，其实现了Ordered接口，AOP切面会根据优先级顺序启用优先级最高的拦截器
10. API请求日志拦截器扩展接口，其实现Ordered接口，AOP切面会根据优先级顺序取优先级最高的拦截器
11. RestTemplate拦截器接口，新增Ordered实现，AOP切面会取优先级最高
12. mybatis-spring-boot-starter版本升级到2.2.2
13. Feign日志拦截器通用接口，新增Ordered接口实现，AOP切面回去优先级最高的
14. Mybatis提供扩展点接口

-----
基础库发布4.0.6
Bug Fixes

1. 新增TTL说明文档及示例demo
2. 化基于大小和时间的策略文件大小的默认值
3. 增加对logback appender日志控制属性配置
4. 对属性配置优化将异步、归档策略、基础配置归类到appender下
5. 升级mysql版本到8.0.27，修复oracle mysql输入验证错误漏洞
6. 升级protobuf版本到3.19.2，解决潜在漏洞
7. 删除oauth2相关模块代码，将代码备份到feature_bak分支
8. logback归档日志文件类LogbackRollingFileAppenderImpl获取归档策略修改为工厂模式
9. logback在整个基础架构中的层级调整，调整logback引用关系
10. mybatis拦截日志组件重构，支持拦截当前接口的父接口或父类，重构拦截器
11. mybatis-spring-boot-starter版本升级
12. AOP切面拦截新增切面增强类、方法切点类、mybatis日志拦截优化重构
13. 多数据源组件改造为最新切面类型，支持向上搜索方法、类、接口上的注解

-----
基础库发布4.0.5
Bug Fixes

1. 解决logback日志组件分组时pattern打印时不可以打印指定具体的类问题
2. 对appender等基础类进行优化调整
3. logback日志组件文件归档策略类型调整优化为指定枚举类型
4. 新增日志文件归档策略类、日志编码格式类
5. 依赖组件升级

-----
基础库发布4.0.4
Bug Fixes

1. 优化logback属性配置，ROOT、Group、Module三种分别定义子类型
2. 获取logback的appenderName方式加入日志类别
3. 优化logback异步属性配置
4. StringHelper帮助类新增截取字符串前部分指定字符数及后面部分指定字符数
5. logback属性配置优化调整
6. 新增logback是否报告logback内部状态信息控制
7. 新增substringBeforeFirst字符串获取方法
8. 优化appender相关类代码

-----
基础库发布4.0.3
Bug Fixes

1. springboot升级到2.6.2
2. logback日志组件优化调整

-----
基础库发布4.0.2
Bug Fixes

1. springcloud版本升级到3.1.0

-----
基础库发布4.0.1
Bug Fixes

1. 多数据源组件优化调整
2. SPI示例编写
3. 升级springboot版本2.6.1及springcloud版本3.0.4

-----
基础库发布3.11.5

1. Redis组件代码优化调整
2. 数据源及API切面拦截新增拦截器扩展点（基于ObjectProvider）
3. 新增全链路追踪属性配置类
4. 新增系统编号帮助类
5. 新增系统编号、语言等全局属性

-----
基础库发布3.11.4

1. ContextHolder全局属性新增clientIp、serverIp
2. 基础库所有请求日志新增clientIp、serverIp字段
3. Redis组件新增是否校验连接配置、新增是否共享本地物理连接配置
4. Redis组件包名去掉datasource
5. Redis及数据库多数据源中间件新增异常处理类
6. Redis多数据源组件更新到2.6.0版本，新增连接池开关，组件代码兼容新代码
7. springboot升级到最新版本2.6.0

-----
基础库发布3.11.3

1. 新增Redis数据源指标监控采集能力
2. ContextHolder上下文持有数据有ThreadLocal更改为InheritableThreadLocal，解决子线程拿不到父线程数据的问题
3. RestTemplate新增请求header添加traceId事务唯一编号
4. ContextHolder全局属性存储对象由InheritableThreadLocal更改为阿里开源组件TransmittableThreadLocal，支持多数据源父子线程的继承关系

-----
基础库发布3.11.2

1. 新增RedisPoolBuilderFactory工厂类
2. 对redis多数据源基础组件进行优化调整，使用springboot自动化注入的方式注入ClientResources并在销毁是调用shutdown方法
3. 优化redis多数据源组件工厂类构造方法
4. 新增Redis集群拓扑刷新支持
5. Redis多数据源组件加载优先级调整

-----
基础库发布3.11.1

1. RPC客户端新增LoadBalance负载均衡策略获取客户端连接（RandomLoadBalance）
2. 新增RPC客户端轮询RoundLoadBalance策略
3. RPC客户端支持配置多个服务器地址
4. 系统中所有的pom引用依赖修改为通过dependencyManagement管理
5. 新增IllegalArgumentException非法餐宿异常捕获

-----
基础库发布3.11.0

1. API请求Get方式参数记录
2. PrintExceptionInfo打印异常堆栈信息时真实的异常类信息不完整优化调整
3. RPC客户端及服务端发送的消息体结构更改，返回数据增加IRpcResponse响应实体
4. RPC客户端超时时间属性配置优化，连接方法优化为传递host及port参数

-----
基础库发布3.10.5

1. RPC客户端及服务端InBound时间处理完成后主动添加ReferenceCountUtil.release(msg);释放消息，防止对象内存溢出
2. RPC客户端发送接收消息归类到用户自定义handler
3. RPC客户端记录日志
4. RPC服务端接口调用发生异常日志调整
5. JSONUtils工具类新增parseObject方法可以将Object对象转换为目标对象
6. RPC客户端新增读取超时、连接超时时间属性，并优化相关代码，去掉IRpcHead头中的keepAlive字段

-------
基础库发布3.10.4

1. RPC客户端及服务端代码整合优化
2. RPC请求消息头新增traceId事务唯一标识，将协议中的traceId去除掉
3. RPC服务端代码优化，记录请求响应日志
4. 升级spring-boot版本到2.5.6

------
基础库发布3.10.3

1. Feign基于微服务获取真实url优化
2. 删除FeignCommonUtils工具类

------
2021/10/17
基础库发布3.10.2

1. 新增header常量类
2. 移除线程上下文方法优化调整
3. 耗时统计修改为通过线程上下文方式，优化调整基础类路径及RequestUtils工具类
4. RPC客户端及服务端新增心跳验证
5. RPC服务端新增超时时间（读取请求header中的keepAlive）
6. RPC新增ObjectPoolException异常
7. 优化对象连接池

------
2021/10/10
基础库发布3.10.1

1. netty引用统一调整到emily-spring-boot-core模块
2. demo实例模块名调整
3. 优化RequestHelper获取参数中header bug
4. Feign取header及参数bug优化
5. RequestHelper及RequestUtils中方法调整优化

------
2021/10/10
基础库发布3.10.0

1. RPC服务端开发
2. RPC客户端开发
3. 升级spring-boot到2.5.5版本
4. 定义BasicException将BusinessException定义为其子类

------
2021/10/1
基础库发布3.9.5

1. Feign调用接口解决参数丢失问题

------
2021/09/19
基础库发布3.9.4

1. 新增Redis监控信息
2. 新增Redis监控间隔时间配置支持
3. 新增基于netty的RPC服务
4. 新增@RpcService标注bean为RPC服务注解
5. 新增RpcProviderRegistry注册表存储RPC服务
6. RPC库的common模块更改为core核心模块，服务端代码优化调整
7. 将context模块修改为core模块

------
2021/09/19
基础库发布3.9.3

1. 异常耗时优化调整
2. 新增自定义异常返回值模式（还遗留一些校验异常无法处理问题）
3. feign非servlet上下文请求兼容
4. feign httpclient优化调整
5. 新增SqlSessionFactory帮助类SqlSessionFactoryHelper
6. 优化属性配置相关代码
7. 升级mybatis-spring-boot-starter到2.2.0版本
8. 新增ThreadPoolHelper异步线程池帮助类
9. HttpClient请求包括cloud httpclient请求拦截器优化
10. 所有基础库异步记录日志方式更改为通过ThreadPoolTaskExecutor实现

------
2021/09/12
基础库发布3.9.2

1. Redis多数据源组件序列化hash类型优化调整
2. 优化多数据源组件及新增批处理示例

------
2021/09/05
基础库发布3.9.1

1. 多数据源注解@TargetDataSource支持复合注解@Mapper

------
2021/06/02
基础库发布2.4.8版本

1. 日志组件优化，解决设置日志级别时最低级别不可以打印日志问题，优化日志组件类代码，优化日志代码中锁不起作用问题
2. 日志组件级别调整更改为枚举类型，可以自动提示有哪些级别，并新增off和all两个关闭日志及打开所有级别日志
3. 日志组件调整为可以打印出指定级别的日志
4. 日志组件根据不同的日志级别打印出指定级别以上的日志文件，低级别创建文件夹

------
2021/05/25

基础库发布2.4.7版本

1. 跨域组件请求头修改为默认允许所有
2. 组件销毁和初始化打印日志信息优化
3. 包名由framework更改为infrastructure
4. DateUtils日期工具类优化
5. BusinessException业务异常再非web系统下也可以正常输出自定义异常消息
6. springboot版本升级到2.5.0