# spring-parent
### maven父pom和子pom的版本号批量修改

##### 1 设置新的版本号

```
mvn versions:set -DnewVersion=2.1.1.RELEASE
```

##### 2 撤销设置

```
mvn versions:revert
```

##### 3 提交设置

```
mvn versions:commit
```
------
### 关闭不希望启动的组件
##### 1.关闭Redisson组件
```
@SpringBootApplication(scanBasePackages = {"com.yaomy.sgrain"}, exclude = {RedissonAutoConfiguration.class})
```
------
<h3>Spring相关项目</h3> 

- [spring-boot-control-aop-service AOP切面请求参数返回值组件](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-aop-service)
- [spring-boot-control-rest-service(HttpClient请求组件)](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rest-service)
- [spring-boot-control-returnvalue-service(控制器返回值组件)](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-returnvalue-service)
- [spring-boot-control-logback-service(日志组件)](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-logback-service)
- [spring-boot-control-exception-service(控制器自定义异常组件)](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-exception-service)
- [spring-boot-control-conf-service(自定义配置文件路径加载组件)](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-conf-service)
- [spring-boot-control-common-service（公用工具类）](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-common-service)
- [spring-boot-control-redis-service（Redis相关工具类）](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-redis-service)
- [spring-security-jwt-service （Security JWT模式下认证服务器）](https://github.com/mingyang66/spring-parent/tree/master/spring-security-jwt-service)
- [spring-security-oauth2-server-jwt-service (OAuth2 JWT模式下认证服务器)](https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-server-jwt-service)
- [spring-security-oauth2-resource-jwt-service（OAuth2 JWT模式下资源服务器）](https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-resource-jwt-service)
- [spring-security-oauth2-server-redis-service （OAuth2 Redis模式下认证服务器）](https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-server-redis-service)
- [spring-security-oauth2-resource-redis-service （OAuth2 Redis模式下资源服务器）](https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-resource-redis-service)
- [spring-boot-logback-service(Spring boot logback日志)](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-logback-service)
- [spring-boot-control-threadpool-service 线程池](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-threadpool-service)
- [spring-boot-control-rabbitmq-service（RabbitMQ消息中间件）](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)