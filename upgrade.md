###-----
基础库发布5.0.2
Bug Fixes

1. JsonUtils新增对对象或字符串转为JsonNode对象的支持方法readTree、valueToTree；
2. 新增emily-spring-boot-i18n多语言转换module;
3. 对emily-spring-boot-transfer模块中RestTemplate拦截器支持http、https及单个请求timeout超时时间代码重构；
4. knife4j-openapi3-jakarta-spring-boot-starter包版本更新；
5. 更新emily-spring-boot-parent module模块；
6. com.emily.infrastructure.logback.entity.BaseLogger建造者模式实现方案修改；
7. 移除ResponseHttpEntityMethodReturnValueHandler、ResponseHttpHeadersReturnValueHandler、ResponseMethodReturnValueHandler返回值包装类；
8. com.emily.infrastructure.web.response.entity.BaseResponse类建造者模式实现方案修改；

------
Dependency Upgrades

1. Upgrade to springboot 3.3.5;

###-----
基础库发布5.0.1
Bug Fixes

1. 新增emily-spring-boot-parent module定义插件管理；
2. StringUtils新增countOfContains方法计算字符串包含数量；
3. emily-spring-boot-rateLimiter限流组件添加限流支撑能力；
4. 全局异常处理组件对UndeclaredThrowableException捕获处理受检查异常抛出真实异常信息；

###-----
基础库发布5.0.0
Bug Fixes

1. 新增emily-spring-boot-transfer模块，提供对feign的支持；
2. emily-spring-boot-core模块移除emily-spring-boot-logger日志依赖引用；
3. 移除BeanFactoryUtils工具类；
4. 新增spring-cloud-dependencies依赖管理，去掉consul相关依赖管理；
5. emily-spring-boot-starter中RestTemplate拦截器迁移到emily-spring-boot-transfer
6. 新增emily-spring-boot-validation模块并将emily-spring-boot-starter中valid校验迁移到此模块；
7. emily-spring-boot-core deleted common-io dependency;
8. emily-spring-boot-starter add common-io dependency;
9. 新增emily-spring-boot-web module并且将emily-spring-boot-starter中请求响应全局处理迁移过来；
10. 新增emily-spring-boot-tracing链路追踪模块；
11. 将emily-spring-boot-core重命名为emily-spring-boot-aop;
12. emily-dependencies添加knife4j-openapi2-spring-boot-starter依赖管理；
13. 新增emily-spring-boot-rateLimiter限流组件模块；
14. emily-spring-boot-web中新增emily-spring-boot-aop依赖；

------
Dependency Upgrades

1. Upgrade to springboot 3.3.3;

-----
基础库发布4.4.8
Bug Fixes

1. JsonUtils新增readTree工具方法；
2. DeSensitiveUtils脱敏工具类优化调整；
3. 脱敏工具类中提示Unchecked cast使用@SuppressWarnings("unchecked")注解处理；
4. DataMaskUtils脱敏隐藏工具类判定字符串长度由"abc".length==0修改为”abc“.isEmpty()方法；
5. redis sdk 依赖优化调整，RedisTemplate、StringRedisTemplate、ReactiveRedisTemplate、ReactiveStringRedisTemplate
   四个模板对象支持默认对象注入容器，支持配置标识+模板名称模式获取实例对象；
6. RedisDbFactory新增ReactiveRedisTemplate、ReactiveStringRedisTemplate模板对象支持，优化方法获取默认模板相关代码；
7. Redis SDK新增package-info.java支持；
8. Redis SDK新增服务启动故障分析类RedisIdentifierFailureAnalyzer.java；
9. Logger SDK新增自动化配置类元数据生成支持spring-boot-configuration-processor依赖，只在编译阶段存在；
10. 新增emily-spring模块，添加ClassPathResourceSupport类对类路径、系统文件路径下文件解析；
11. CharacterInfo新增竖线常量VERTICAL;
12. 新增emily-dependencies物料清单module;
13. 新增emily-parent父module;
14. 新增emily-project module将oceansky相关java module归类到此模块下；
15. 新增emily-spring-boot-project 模块，将spring-boot相关模块归类到此模块下；
16. 新增emily-spring-boot-sample模块，将demo相关模块归类到此模块下；

------
Dependency Upgrades

1. Upgrade to springboot 3.3.2;
2. Upgrade to maven-gpg-plugin 3.2.4;
3. Upgrade to maven-javadoc-plugin 3.7.0;
4. Upgrade to maven-compiler-plugin 3.13.0;
5. Upgrade to maven-source-plugin 3.3.1;

-----
基础库发布4.4.3
Bug Fixes

1. 新增ComputeUtils.getEffectiveValue获取字符串数字有效位数工具方法；
2. com.emily.infrastructure.captcha.CaptchaUtils.convertStreamToBase64新增将图形验证码图片流转换为Base64字符串方法；
3. ocean-sensitive组件对脱敏工具类SensitiveUtils、DeSensitiveUtils新增指定外层包装未标记脱敏注解但是会对内层标记脱敏注解的类字段进行脱敏；
4. SensitiveUtils、DeSensitiveUtils 脱敏工具类支持对指定包装外层包了Map、List、Collection、Array等集合类型的数据进行脱敏；
5. ComputeUtils工具类新增round、toPercentage保留小数位数及转换为百分比方法；
6. 新增自带默认值ComputeUtils.getEffectiveValue(java.lang.String, java.lang.String)小数位数工具方法；
7. ComputeUtils工具类新增rounding相关方法对BigDecimal进行舍入处理；
8. PrintLoggerUtils打印日志类对Logger对象的初始化修改为静态内部类模式延迟加载。
9. 各个模块新增module-info.java模块系统支持；。、

------
Dependency Upgrades

1. Upgrade to logback 1.5.6;
2. Upgrade to slf4j-api 2.0.13;
3. Upgrade to spring-boot 3.3.0;
4. Upgrade to druid-spring-boot-3-starter 1.2.23;

-----
基础库发布4.4.2
Bug Fixes

1. com.emily.infrastructure.core.helper.ServletHelper.getMethodArgs方法参数脱敏注解处理优化；
2. com.emily.infrastructure.core.helper.RequestUtils.getHeaders方法新增获取请求头信息；
3. DefaultRequestMethodInterceptor全局拦截器优化调整；
4. DefaultMybatisMethodInterceptor全局拦截器优化调整，对获取参数先后数据做调整；
5. 新增@IsAccountCode判定是否符合指定条件的账号参数校验注解；
6. 注解@IsPrefix重命名为@IsPrefixes;
7. 注解@IsSuffix重命名为@IsSuffixes;
8. DateConvertUtils工具类新增toInstant方法；
9. DatePatterInfo新增常量YYYY_MM_DD_T_HH_MM_SS_Z支持ISO-8601;
10. springboot自定义注解@IsInt、@IsLong、@IsDouble新增min、max参数，支持最小值和最大值；
11. 注解@IsInt、@IsLong、@IsDouble新增allows参数，支持特例值；
12. 修改返回值包装类名及注解名；
13. 自定义注解优化调整；
14. 请求拦截器代码逻辑优化调整；

------
Dependency Upgrades

1. Upgrade to springboot 3.2.5;
2. Upgrade to druid-spring-boot-3-starter 1.2.21;
3. Upgrade to logback 1.5.0;
4. Upgrade to slf4j-api 2.21.1;
5. Upgrade to jackson 2.15.4;

-----
基础库发布4.4.1
Bug Fixes

1. LuaScriptTools新增基于ZSET有序集合的环形节点；
2. LuaScriptTools新增基于TTL查询永久有效的key的lua脚本及方法；
3. LuaScriptTools新增基于list列表有序环形结构及lua脚本；
4. LuaScriptTools新增基于redis lua脚本的加锁解锁脚本及逻辑；
5. LuaScriptTools新增基于redis scan指令的批量获取数据lua脚本及指令；
6. LuaScriptTools中tryGetLock、releaseLock获取和释放锁方法及lua脚本优化调整，解决A线程可能会释放B线程持有的锁问题；
7. 基础库中的所有建造者模式类的create方法都更改为对应实体类的newBuilder();
8. CaptchaBuilder类建造方法优化调整；
9. emily-spring-boot-parent默认引入spring-boot-starter-test单元测试依赖；
10. DateCompareUtils工具类新增isAfter、isBefore、isEqual方法；
11. oceansky-sensitive脱敏工具类新增@JsonMapField注解专业对Map集合数据脱敏；
12. oceansky-sensitive单元测试工具升级到junit5;
13. 移除com.emily.infrastructure.logger.configuration.type.LevelType枚举类，并由org.slf4j.event.Level替换；
14. HeaderInfo新增IP相关请求头，RequestUtils工具类方法优化并新增相关单元测试类；
15. RequestHelper重名名为ServletHelper;
16. com.emily.infrastructure.core.helper.RequestUtils.getRealClientIp方法新增获取请求真实IP地址；
17. emis-spring-boot-starter SDK新增参数校验注解@IsInclude、@IsLocalDate、@IsLocalDateTime、@IsLocalTime;
18. emis-spring-boot-starter SDK新增参数校验注解@IsInt、@IsLong、@IsDouble、@IsBigDecimal、@IsPrefix、@IsSuffix;
19. 全局异常捕获新增ErrorResponseException异常类，统一处理异常信息；
20. emi-spring-boot-starter SDK新增@IsBeforeEndDate日期大小比较注解；
21. 新增PrintLoggerUtils打印全局日志工具类，方便开发人员调试；
22. delete useless code BaseLoggerBuilder;
23. delete useless code BaseResponseBuilder;

------
Dependency Upgrades

1. Upgrade to springboot 3.2.0;
2. Upgrade to mybatis-spring-boot-starter 3.0.3;
3. Upgrade to jackson 2.14.3;
4. Upgrade to common-langs 3.13.0;
5. Upgrade to logback 1.4.14;
6. Upgrade to transmittable-thread-local 2.14.4;
7. Upgrade to SpringCloud 4.1.0;
8. Upgrade to springboot 3.2.1;
9. Upgrade to transmittable-thread-local 2.14.5;

-----
基础库发布4.4.0
Bug Fixes

1. LogbackPropertyBuilder新增create创建对象方法；
2. LogbackContext的getLogger方法修改获取logger对象双重锁检查方法代码；
3. logger sdk code optimize;
4. logger policy构建对象重构为builder模式；
5. logger encode构建对象重构为builder模式；
6. logger filter构建对象重构为builder模式；
7. 新增CommonKeys解析logger name工具类；
8. LoggerContext类代码重构优化调整、修改获取loggerName方法；
9. 新增FixedWindow固定窗口大小归档策略类；
10. RedisInfo常量类重命名为RedisCommonKeys;
11. 新增IOC容器DefaultListableBeanFactory的工具类BeanFactoryUtils；
12. 将Redis sdk中的所有DefaultListableBeanFactory容器工厂替换为BeanFactoryUtils类；
13. 新增Redis序列化SerializationUtils工具，修改RedisTemplate和StringRedisTemplate实例bean的序列化方式；
14. redis sdk beanname及join拼接字符串方法优化；
15. redis sdk 新增validateConnection、shareNativeConnection、eagerInitialization三个属性配置，控制连接、初始化、校验；
16. redis sdk core code modify;
17. 新增RedisProperties配置类继承官方的RedisProperties配置类，添加自定义属性配置，修改自定义sdk代码；
18. redis sdk RedisConnectionDetails属性修改传递对象模式；
19. redis sdk 新增LuaScriptTool工具类包含限流、环形节点结构；

------

Dependency Upgrades

1. Upgrade to springboot 3.1.5

-----
基础库发布4.3.9
Bug Fixes
一、 RabbitMQ SDK支持连接故障自动恢复能力

1. 新增如下4个属性配置：

   ```properties
   #设置TCP连接超时时间，默认：60000ms
   spring.emis.rabbitmq.connection-timeout=60000
   #启用或禁用连接自动恢复，默认：true
   spring.emis.rabbitmq.automatic-recovery=true
   #设置连接恢复时间间隔，默认：5000ms
   spring.emis.rabbitmq.network-recovery-interval=5000
   #启用或禁用拓扑恢复，默认：true【拓扑恢复功能可以帮助消费者重新声明之前定义的队列、交换机和绑定等拓扑结构】
   spring.emis.rabbitmq.topology-recovery=true
   ```

2. 创建CachingConnectionFactory对象时设置（1）的4个属性设置；

3. 新增DefaultMqConnectionListener连接监听器类，对onCreate、onClose、onShutDown、onFailed四个方法监听并记录三方日志，方便在日志平台观察rabbitmq的故障恢复的全过程；

4. 新增DefaultMqExceptionHandler RabbitMQ全局异常处理，将希望记录到日志文件的日志记录到三方日志，方便日志平台查看；

5. ContextWrapper优化为只对非servlet上下文场景下使用；

6. RabbitMQ SDK重构，默认支持springboot官方支持的使用方法；支持直接使用注解模式发送消息，支持默认监听器不用配置连接器工厂类；

7. 新增RabbitMqRetryTemplateCustomizer作为RetryTemplate自定义配置的一部分；

8. 新增PublisherRetryListener发布放重试监听器；

9. springboot升级到3.1.3版本，并对整体做架构升级，jdk升级到17；

10. logback sdk代码优化调整；

11. logger sdk新增withJansi属性配置，默认不开启jansi；

12. 修复RabbitMQ SDK中RabbitTemplate配置bug;
13. 动态多数据源SDK代码优化；

------

Dependency Upgrades

1. springboot升级到3.1.4版本；
2. druid升级到1.2.20版本；

-----
基础库发布4.3.8
Bug Fixes

1. maven-gpg-plugin插件升级到3.1.0版本；
2. pom.xml指定源代码编码格式配置project.build.sourceEncoding；
3. maven-resources-plugin插件研究，添加使用注解；
4. logger sdk的appender name优化重构；
5. 将TTL在线程池、父子线程场景下取消Interitable继承能力，防止由于线程复用导致脏读、OOM问题；
6. SystemNumberHelper如果在非容器上下文发生异常返回空字符串；
7. 新增ContextWrapper对传入的线程进行TTL修饰并执行；
8. 新增ContextHolderBuilder类，替换直接使用ContextHolder类；
9. ObjectUtils工具类新增defaultIfNull方法；
10. 修改new BaseLoggerBuilder()为BaseLoggerBuilder.create()方法；
11. ContextWrapper包装执行代码优化新增业务逻辑上的servlet设置；
12. transmittable-thread-local升级版本到2.14.3；
13. optimize ContextHolderBuilder code;
14. optimize cloud feign code;
15. 将spring-boot-starter-validation依赖由core模块更改为emily-spring-boot-starter模块；
16. commons-lang3版本升级到3.13.0;
17. 新增ContextTransmitter对上线文进行备份、设置、恢复类；
18. 新增ParameterInterceptor拦截器，对API请求进行上下文初始化及移除操作；
19. 上下文新增servlet阶段标识，重构API、全局异常处理对阶段标识处理逻辑；
20. ContextHolder新增spentTime耗时字段，API上下文中spentTime相关处理移除，统一使用上线文中的耗时字段；
21. 日志记录全部标准化记录到指定的日志文件中；
22. GlobalExceptionCustomizer全局异常处理记录日志优化，对HttpRequestMethodNotSupportedException做前置和后置特殊处理；
23. DefaultRequestMethodInterceptor拦截器异常特殊处理，确保返回数据符合标准格式；

-----
基础库发布4.3.7
Bug Fixes

1. logger sdk新增name属性设置；
2. logger sdk新增对Marker的支持，支持接受标记的日志记录入文件，支持拒绝标记的日志记录入文件，新增如下配置：

```properties
#接受指定标记的日志记录到文件中
spring.emily.logger.marker.accept-marker=
#拒绝标记的日志记录到文件中
spring.emily.logger.marker.deny-marker=
```

3. springboot版本升级到2.7.14；
4. 升级maven-javadoc-plugin插件版本到3.3.2版本，并修改如果遇到文档错误不暂停构建参数为failOnError；
5. oceansky-logger、emily-spring-boot-starter sdk的javadoc文档优化；
6. emily-spring-boot-datasource SDK的javadoc文档优化；
7. oceansky-sensitive SDK的javadoc文档优化；
8. sdk对javadoc文档注解优化调整；

-----
基础库发布4.3.6
Bug Fixes

1. 删除自动化配置类RouteWebMvcAutoConfiguration、LookupPathAutoConfiguration；
2. oceansky-common工具类新增PathUtils格式化路径方法；
3. 返回值包装类ResponseWrapperAdviceHandler支持application/vnd.spring-boot.actuator.v3+json媒体类型；
4. 新增oceansky-logger基于logback的日志组件，并提供纯Java使用日志SDK的方法；
5. emily-spring-boot-logger组件SDK重构为依赖oceansky-logger并进行starter化；
6. oceansky-logger组件支持重复初始化时重置调原来LoggerContext上下文对象的初始化信息；
7. emily-spring-boot-logger组件支持通过Binder类将Environment变量中的变量转换为一个实例变量；
8. oceansky-logger SDK组件支持对归档日志文件进行压缩处理，支持GZ、ZIP格式压缩；
9. logger sdk新增ConfigurationAction类，支持debug、logback.debug调试信息，向LoggerContext中新增OnConsoleStatusListener监听器监听打印调试信息；
10. logger sdk内部debug模式和是否开启内部状态信息合并，并优化代码；
11. logger sdk对root logger中的console appender兼容大小写类型；
12. logger sdk新增对packingData属性的支持；
13. root logger剔除console appender，如果初始化默认appender name为大写时兼容；
14. logger sdk的appender name拼接中如果filename为null，转换为空字符串；
15. logger sdk appender name 拼接规则重构为：分组.路径.文件名.日志级别；
16. logger sdk logger name 拼接规则：分组.路径.文件名（可能不存在）.类名（包括包名）；
17. LogbackAppender重命名为LogbackProperty，新增loggerName属性，重构核心类属性传递逻辑；
18. logger sdk 路径统一格式化；

-----
基础库发布4.3.5
Bug Fixes

1. emily-spring-cloud-starter组件删除httpclient对自定义负载均衡支持；
2. oceansky-date组件新增将时间戳转换为Date对象方法；
3. oceansky-sensitive实体类脱敏组件抛出异常方式优化，新增acquireElseGet方法;
4. 新增oceansky-common组件，新增PropertiesUtils工具类用于读取各种路径下的配置文件，支持properties、xml、yaml;
5. 删除demo实例程序中的FileHelper相关代码；
6. common组件新增CollectionUtils工具类；
7. 优化RequestUtils.getHeader方法参数；
8. 新增字符串工具类StringUtils，新增leftPad、rightPad、isEmpty、isBlank、length、isNumeric、replace、getBytes等工具方法；
9. 字符串工具类StringUtils新增trim、trimToEmpty、trimToNull工具方法；
10. 新增ObjectUtils工具类，包含isNull、isNotNull、isEmpty、isNotEmpty工具方法；
11. 新增监听器EnvironmentChangeApplicationListener解决ApplicationContext重新创建导致原来的ApplicationContext失效；
    异常示例：
    org.springframework.context.annotation.AnnotationConfigApplicationContext@7a8414ea has been closed already
12. StringUtils工具类新增split方法；
13. 新增ServletRequestHandledApplicationListener监听器，监听ServletRequestHandledEvent事件；
14. ThreadContextHolder类重命名为LocalContextHolder;
15. springboot全局异常处理新增HandlerMethod参数，修改异常返回值数据类型为Object,兼容@ApiResponseWrapperIgnore注解忽略异常返回包装；
16. guava-jre升级到32.1.0版本；
17. 新增正则表达式匹配帮种类RegexPathMatcher；
18. 删除修改响应数据类型编码自动化配置类EmilyMappingJackson2HttpMessageConverterAutoConfiguration；
19. WebMvcAutoConfiguration组件优化调整，@ApiPrefix注解更改为ApiPathPrefixIgnore并做逻辑处理；
20. UUIDUtils工具类去除三方依赖并迁移到oceansky-common包；
21. springboot版本升级到2.7.13；
22. mybatis-spring-boot-starter升级到2.3.1；
23. druid-spring-boot-starter升级到1.2.18；
24. 返回值包装处理程序兼容响应Content-Type为Image/gif、image/jpeg、image/png类型；
25. 最外层添加maven-compiler-plugin插件，控制maven编译之后保留方法参数名；

-----
基础库发布4.3.4
Bug Fixes

1. 日期SDK DatePatternType枚举类修改为DatePatternInfo常量类；
2. 日期SDK DateComputeUtils工具类新增minusMillis(Instant instant1, Instant instant2)工具方法；
3. 日期SDK DateCompareUtils工具类新增compareTo(Instant instant1, Instant instant2)工具方法；
4. SDK中设计到耗时计算的组件更改为使用DateComputeUtils.minusMills工具方法；
5. springboot版本升级到2.7.12；
6. 日期SDK DateConvertUtils新增两个combine连接方法，将日期对象或字符串和时间对象和字符串拼接到一起；
7. 日期SDK新增DateComputeUtils.getRemainTimeOfDay()方法；
8. RequestUtils工具类新增getHeader请求方法获取请求头；
9. DataMaskUtils脱敏手机号、邮箱、银行卡、身份证等工具方法重构；
10. oceansky-sensitive脱敏组件去除掉guava依赖包引用，优化SDK组件代码；
11. oceansky-date日期组件SDK去除掉commons-lang3依赖引用，将Date类型日期转换调整为使用SimpleDateFormat;
12. oceansky-captcha图形验证码组件去掉commons-lang3依赖引用；
13. 删除掉oceansky-commons包，并修改其依赖；

-----
基础库发布4.3.3
Bug Fixes

1. 新增oceansky-date SDK组件，新增DateUtils工具类，内部包含14个工具方法，涉及字符串转日期，日期转字符串；
2. 新增DateConvertUtils日期相互转换工具类；
3. 新增DateCompareUtils日期比大小工具类；
4. 新增DateComputeUtils日期计算相关工具类；
5. 新增DateConvertUtils.toLocalDateTime(java.time.LocalDateTime, java.time.ZoneId)根据时区转换日期方法；
6. BaseLoggerBuilder、BaseResponseBuilder建造者模式类方法统一添加with前缀，并修改所有使用到的点；
7. 脱敏SDK新增@JsonNullField注解，可以将指定的非原始字段值置为null;

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
public int postList(@RequestBody List<User> list) {
    System.out.println(JSONUtils.toJSONPrettyString(list));
    return 0;
}

@PostMapping("postArray")
public int postList(@RequestBody User[] list) {
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