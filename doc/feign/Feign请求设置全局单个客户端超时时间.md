### Feign请求设置全局|单个客户端超时时间

在使用@FeignClient时，可以通过contextId属性为不同的服务设置不同的超时时间，默认不配置情况下contextId值为default：

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

> contextId默认情况下是default，即：全局超时时间配置，如果需要自定义客户端超时时间需配置与default平行级别的配置，如：custom

##### FeignClient默认使用全局配置：

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

##### FeignClient使用自定义超时配置：

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

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)