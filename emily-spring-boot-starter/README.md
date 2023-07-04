#### 解锁新技能《springboot基于ResponseBodyAdvice的AOP切面返回值包装》

> 在项目开发过程中我们会对返回值进行统一的包装处理，对最外层加上status、message、data、spentTime等统一个是的包装；当前SDK支持两种方案，一种基于适配器模式实现，一种基于AOP切面实现，本文只对AOP模式讲解，适配器方案参考源码；

##### 一、开源SDK依赖POM

```xml
<!-- https://mvnrepository.com/artifact/io.github.mingyang66/emily-spring-boot-starter -->
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-starter</artifactId>
    <version>4.3.5</version>
</dependency>

```

##### 二、开源SDK依赖配置

```properties
# 返回值包装SDK开关，默认：true
spring.emily.response.enabled=true
# 基于适配器模式的实现方案，默认：false
spring.emily.response.enabled-adapter=false
# 基于AOP切面的实现方案，默认：true
spring.emily.response.enabled-advice=true
# 排除指定url对返回值进行包装，支持正则表达式
spring.emily.response.exclude=abc/a.html
```

##### 三、基于AOP实现方案

```java
@RestControllerAdvice
public class ResponseWrapperAdviceHandler implements ResponseBodyAdvice<Object> {

    private final ResponseWrapperProperties properties;

    public ResponseWrapperAdviceHandler(ResponseWrapperProperties properties) {
        this.properties = properties;
    }

    /**
     * 指定支持的数据类型
     *
     * @param returnType    the return type
     * @param converterType the selected converter type
     * @return true-支持所有类型
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * -------------------------------------------------
     * 参数说明：
     * body:如果返回值是ResponseEntity类型，则此方法拿到的是去除外层ResponseEntity后的body
     * response:可以通过此对象更改响应对象请求头信息
     * -------------------------------------------------
     *
     * @param body                  the body to be written
     * @param returnType            the return type of the controller method
     * @param selectedContentType   the content type selected through content negotiation
     * @param selectedConverterType the converter type selected to write to the response
     * @param request               the current request
     * @param response              the current response
     * @return 包装处理后的数据
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        BaseResponseBuilder<Object> builder = new BaseResponseBuilder<>()
                .withStatus(HttpStatusType.OK.getStatus())
                .withMessage(HttpStatusType.OK.getMessage());

        return builder.withData(body).build();
    }
}
```

通过上述代码可以实现将返回值统一包装为我们期望的统一格式，返回值示例：

```json
{
    "status": 0,
    "message": "SUCCESS",
    "data": {
        "username": "田晓霞",
        "password": "密码"
    },
    "spentTime": 3
}
```

如果返回值是字符串，那又会发生什么问题？看如下报错：

```sh
org.springframework.http.converter.StringHttpMessageConverter.addDefaultHeaders(StringHttpMessageConverter.java:44) 
class com.emily.infrastructure.core.entity.BaseResponse cannot be cast to class java.lang.String (com.emily.infrastructure.core.entity.BaseResponse is 
in unnamed module of loader 'app'; java.lang.String is in module java.base of loader 'bootstrap')
org.springframework.http.converter.AbstractHttpMessageConverter.write(AbstractHttpMessageConverter.java:211)
org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor.writeWithMessageConverters(AbstractMessageConverterMethodProcessor.java:293)
org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor.handleReturnValue(HttpEntityMethodProcessor.java:219)
org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite.handleReturnValue(HandlerMethodReturnValueHandlerComposite.java:78)
org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:135)
org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895)
```

主要看BaseResponse cannot be cast to class java.lang.String 这个报错，我们可以看到是由于字符串不可以转换为BaseResponse的原因；这是由于AOP切面拿到的数据ContentType是text/plain,解析器是使用StringHttpMessageConverter来解析；

解决方案是：

- 将字符串包装后的BaseResponse转换为json字符串；
- 将返回数据的ContentType转换为application/json;

代码示例如下：

```java
if (MediaType.TEXT_PLAIN.equals(selectedContentType)) {
      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      return JsonUtils.toJSONString(builder.withData(body).build());
  }
```

SDK对其它特殊场景的支持看如下完整的代码：

```java
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 如果返回值已经是BaseResponse类型(包括控制器直接返回是BaseResponse和返回是ResponseEntity<BaseResponse>类型)，则直接返回
        if (body instanceof BaseResponse) {
            return body;
        }
        // 如果控制器上标注类忽略包装注解，则直接返回
        else if (returnType.hasMethodAnnotation(ApiResponseWrapperIgnore.class)) {
            return body;
        }
        // 如果请求URL在指定的排除URL集合，则直接返回
        else if (RegexPathMatcher.matcherAny(properties.getExclude(), request.getURI().getPath())) {
            return body;
        }
        // 如果返回值是数据流类型，则直接返回
        else if (MediaType.APPLICATION_OCTET_STREAM.equals(selectedContentType)) {
            return body;
        }

        //------------------------------------------对返回值进行包装处理分割线-----------------------------------------------------------------
        BaseResponseBuilder<Object> builder = new BaseResponseBuilder<>()
                .withStatus(HttpStatusType.OK.getStatus())
                .withMessage(HttpStatusType.OK.getMessage());
        // 如果返回值是void类型，则直接返回BaseResponse空对象
        if (returnType.getParameterType().equals(Void.class)) {
            return builder.build();
        }
        // 如果是字符串类型，将其包装成BaseResponse类型
        // 如果是字符串类型，外层有ResponseEntity包装，将其包装成BaseResponse类型
        else if (MediaType.TEXT_PLAIN.equals(selectedContentType)) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JsonUtils.toJSONString(builder.withData(body).build());
        }

        return builder.withData(body).build();
    }
```

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)