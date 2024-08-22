##### 一、Feign组件

官方地址：[https://docs.spring.io/spring-cloud-openfeign/reference/spring-cloud-openfeign.html](https://docs.spring.io/spring-cloud-openfeign/reference/spring-cloud-openfeign.html)

核心初始化类：FeignClientFactoryBean、FeignAutoConfiguration

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

# 连接超时时间，默认：60000毫秒
spring.cloud.openfeign.client.config.custom.connect-timeout=5000
# 读取超时时间，默认：10000毫秒
spring.cloud.openfeign.client.config.custom.read-timeout=1000
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

