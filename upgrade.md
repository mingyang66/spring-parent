### 
-----
基础库发布4.0.7
Bug Fixes
1. 优化数据源注解@TargetDataSource注解说明
2. 升级springboot到2.6.3版本
3. netty升级到4.1.73.Final
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