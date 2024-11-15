##### 一、Spring Cloud OpenFeign 4组件

官方地址：[https://docs.spring.io/spring-cloud-openfeign/reference/spring-cloud-openfeign.html](https://docs.spring.io/spring-cloud-openfeign/reference/spring-cloud-openfeign.html)

核心初始化类：FeignClientFactoryBean、FeignAutoConfiguration、FeignClientsConfiguration、HttpClient5FeignConfiguration

- 开关配置

```properties
# 组件开关，默认：true
spring.emily.transfer.feign.enabled=true
```

- 支持全局设置超时时间及单个FeignClient设置超时时间

```properties
# 默认配置
spring.cloud.openfeign.client.default-config=default
# 连接超时时间，默认：60000毫秒
spring.cloud.openfeign.client.config.default.connect-timeout=60000
# 读取超时时间，默认：10000毫秒
spring.cloud.openfeign.client.config.default.read-timeout=10000
# 日志级别，默认：none
spring.cloud.openfeign.client.config.default.logger-level=full

# 连接超时时间，默认：60000毫秒
spring.cloud.openfeign.client.config.test.connect-timeout=5000
# 读取超时时间，默认：10000毫秒
spring.cloud.openfeign.client.config.test.read-timeout=1000
# 日志级别，默认：none
spring.cloud.openfeign.client.config.test.logger-level=full
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

- 调用三方接口默认采用HttpURLConnection

```tex
调用链路(倒序)：
feign.Client的默认实现类feign.Client.Default
org.springframework.cloud.openfeign.FeignCachingInvocationHandlerFactory#create
```

- Spring Cloud OpenFeign 4不在支持Apache HttpClient 4

[https://docs.spring.io/spring-cloud-openfeign/reference/spring-cloud-openfeign.html](https://docs.spring.io/spring-cloud-openfeign/reference/spring-cloud-openfeign.html)

> Starting with Spring Cloud OpenFeign 4, the Feign Apache HttpClient 4 is no longer supported. We suggest using Apache
> HttpClient 5 instead.

- Appache HttpClient 5使用

添加依赖：

```xml
        <dependency>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-hc5</artifactId>
        </dependency>
```

org.springframework.cloud.openfeign.FeignAutoConfiguration.HttpClient5FeignConfiguration对应配置类开关打开：

```
spring.cloud.openfeign.httpclient.hc5.enabled=true
```

> 开关默认就是打开的，只要引用依赖包自动生效；Client的默认实现类是feign.hc5.ApacheHttp5Client

官方介绍：

> When it comes to the Apache HttpClient 5-backed Feign clients, it’s enough to ensure HttpClient 5 is on the classpath,
> but you can still disable its use for Feign Clients by setting `spring.cloud.openfeign.httpclient.hc5.enabled`
> to `false`. You can customize the HTTP client used by providing a bean of
> either `org.apache.hc.client5.http.impl.classic.CloseableHttpClient` when using Apache HC5.

连接池及连接 相关配置(初始化配置类在HttpClient5FeignConfiguration)：

```properties
# 连接池最大连接数，默认：200
spring.cloud.openfeign.httpclient.max-connections=200
# 每条路由的最大连接数，默认:50
spring.cloud.openfeign.httpclient.max-connections-per-route=50
# 连接池中空闲连接存活时间，默认：900
spring.cloud.openfeign.httpclient.time-to-live=900
# 连接池中空闲连接存活时间单位，默认：seconds
spring.cloud.openfeign.httpclient.time-to-live-unit=seconds
# 是否禁用SSL验证，默认：false
spring.cloud.openfeign.httpclient.disable-ssl-validation=false
# 连接超时时间，默认：2000  --会被feign的设置覆盖
spring.cloud.openfeign.httpclient.connection-timeout=2000


# 开启Apache HttpClient 5，默认：true
spring.cloud.openfeign.httpclient.hc5.enabled=true
# 连接请求超时时间，默认：3 --从请求连接池中获取连接等待的超时时间
spring.cloud.openfeign.httpclient.hc5.connection-request-timeout=3
# 连接请求超时时间单位，默认：分钟
spring.cloud.openfeign.httpclient.hc5.connection-request-timeout-unit=minutes
# socket超时时间，默认：5 --等待服务器响应超时时间
spring.cloud.openfeign.httpclient.hc5.socket-timeout=5
# socket超时时间单位，默认：seconds
spring.cloud.openfeign.httpclient.hc5.socket-timeout-unit=seconds
# 连接池重用策略，默认：fifo
spring.cloud.openfeign.httpclient.hc5.pool-reuse-policy=fifo
# 连接池并发策略，默认：strict
spring.cloud.openfeign.httpclient.hc5.pool-concurrency-policy=strict
```

- @SpringQueryMap注解

将GetMapping请求对应的多个请求参数映射为POJO类或Map对象：

```java
@FeignClient(name = "test", url = "http://127.0.0.1:8080/", contextId = "test")
public interface FeignRequestHandler {
    @GetMapping(value = "api/feign/test")
    String get(@SpringQueryMap Map<String, Object> params);
}
```

- @RequestParam指定单个请求参数

```java
@FeignClient(name = "test", url = "http://127.0.0.1:8080/", contextId = "test")
public interface FeignRequestHandler {
    @GetMapping(value = "api/feign/test")
    String get(@RequestParam("name") String name);
}
```

- @RequestHeader注解指定请求头

```java
@FeignClient(name = "test", url = "http://127.0.0.1:8080/", contextId = "test")
public interface FeignRequestHandler {
    @GetMapping(value = "api/feign/test")
    String get(@RequestHeader HttpHeaders headers, @RequestParam("name") String name);
}

```

- @PathVariable指定路径变量

```java
@FeignClient(name = "test", url = "http://127.0.0.1:8080/", contextId = "test")
public interface FeignRequestHandler {
    @GetMapping(value = "api/feign/test/{name}")
    String get(@PathVariable String name);
}
```

- @MatrixVariable指定接收矩阵变量

```java
@FeignClient(name = "test", url = "http://127.0.0.1:8080/", contextId = "test")
public interface FeignRequestHandler {
    @GetMapping(value = "api/feign/test/{name}")
    String get(@MatrixVariable Map<String, List<String>> params);
}
```

请求url如：api/feign/test/myname,key1=value1a,value1b;key2=value2

- @ReqeustPart支持请求为multipart/form-data的请求变量

```java
@FeignClient(name = "test", url = "http://127.0.0.1:8080/", contextId = "test")
public interface FeignRequestHandler {
    @GetMapping(value = "api/feign/test/{name}")
    String get(@RequestPart MultipartFile params, @PathVariable("name") String name);
}
```

- RequestInterceptor和ResponseInterceptor如何初始化

  org.springframework.cloud.openfeign.FeignClientFactoryBean#configureUsingConfiguration

```java
	protected void configureUsingConfiguration(FeignClientFactory context, Feign.Builder builder) {
		...
		Map<String, RequestInterceptor> requestInterceptors = getInheritedAwareInstances(context,
				RequestInterceptor.class);
		if (requestInterceptors != null) {
			List<RequestInterceptor> interceptors = new ArrayList<>(requestInterceptors.values());
			AnnotationAwareOrderComparator.sort(interceptors);
			builder.requestInterceptors(interceptors);
		}
		ResponseInterceptor responseInterceptor = getInheritedAwareOptional(context, ResponseInterceptor.class);
		if (responseInterceptor != null) {
			builder.responseInterceptor(responseInterceptor);
		}
    ...
	}
```

拦截器具体调用在feign.SynchronousMethodHandler#executeAndDecode方法：

```java
    Object executeAndDecode(RequestTemplate template, Request.Options options) throws Throwable {
      	//调用请求拦截器
        Request request = this.targetRequest(template);
        if (this.logLevel != Level.NONE) {
            this.logger.logRequest(this.metadata.configKey(), this.logLevel, request);
        }

        long start = System.nanoTime();

        Response response;
        try {
            response = this.client.execute(request, options);
            response = response.toBuilder().request(request).requestTemplate(template).build();
        } catch (IOException var9) {
            IOException e = var9;
            if (this.logLevel != Level.NONE) {
                this.logger.logIOException(this.metadata.configKey(), this.logLevel, e, this.elapsedTime(start));
            }

            throw FeignException.errorExecuting(request, e);
        }

        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        //调用响应拦截器
        return this.responseHandler.handleResponse(this.metadata.configKey(), response, this.metadata.returnType(), elapsedTime);
    }
```

