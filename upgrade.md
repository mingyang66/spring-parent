### 
------
基础库发布3.10.5
1. RPC客户端及服务端InBound时间处理完成后主动添加ReferenceCountUtil.release(msg);释放消息，防止对象内存溢出
2. RPC客户端发送接收消息归类到用户自定义handler
3. RPC客户端记录日志
4. RPC服务端接口调用发生异常日志调整
5. JSONUtils工具类新增parseObject方法可以将Object对象转换为目标对象
------
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