### Feign拦截器RequestInterceptor优先级排序详解

> RequestInterceptor接口是Feign请求拦截接口，可以在请求发送之前添加header、记录日志等操作；

##### 一、RequestInterceptor接口

```java
public interface RequestInterceptor {

  /**
   * 每个请求都会调用apply方法
   */
  void apply(RequestTemplate template);
}
```

其实现类有BaseRequestInterceptor抽象拦截器，主要用来添加子类的header信息，其实现类有两个，FeignAcceptGzipEncodingInterceptor用来添加
Accept-Encoding、gzip、deflate头信息，FeignContentGzipEncodingInterceptor用来添加Content-Encoding、gzip、deflate头信息；BasicAuthRequestInterceptor用来添加认证Authorization头信息；OAuth2FeignRequestInterceptor用来添加OAuth2认证相关信息；这五个默认实现只有开启相关配置后才可以启用，优先级是最低的；

##### 二、RequestInterceptor接口实现类优先级

自定义拦截器会希望有一个优先级顺序，那如何确定优先级呢？可以通过PriorityOrdered>Ordered>
@Order来设置优先级顺序，但是系统已经实现的优先级因为没有设置上述排序字段，所以优先级永远是最低的；

org.springframework.cloud.openfeign.FeignClientFactoryBean#configureUsingConfiguration方法在系统启动时排序：

```java
		if (requestInterceptors != null) {
			List<RequestInterceptor> interceptors = new ArrayList<>(requestInterceptors.values());
      // 对拦截器进行排序
			AnnotationAwareOrderComparator.sort(interceptors);
			builder.requestInterceptors(interceptors);
		}
```

feign.SynchronousMethodHandler#targetRequest方法在发送请求时轮询调用拦截器apply方法：

```java
  Request targetRequest(RequestTemplate template) {
    for (RequestInterceptor interceptor : requestInterceptors) {
      interceptor.apply(template);
    }
    return target.apply(template);
  }
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)