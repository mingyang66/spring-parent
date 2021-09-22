### 
------
2021/09/19
基础库发布3.9.4
1. 新增Redis监控信息
2. 新增Redis监控间隔时间配置支持
3. 新增基于netty的RPC服务
4. 新增@RpcService标注bean为RPC服务注解
5. 新增RpcProviderRegistry注册表存储RPC服务
6. RPC库的common模块更改为core核心模块，服务端代码优化调整
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