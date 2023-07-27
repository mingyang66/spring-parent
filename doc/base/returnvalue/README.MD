### Spring boot控制器返回值统一包装处理之HandlerMethodReturnValueHandler

HandlerMethodReturnValueHandler是一个控制器返回值处理程序接口，用来处理spring-boot控制器请求和返回值的处理，其中控制返回值的有两个实现类RequestResponseBodyMethodProcessor
、HttpEntityMethodProcessor（还有其它处理程序），RequestResponseBodyMethodProcessor类是控制普通的返回值类型，HttpEntityMethodProcessor顾名思义就是控制ResponseEntity返回值类型；

通常我们希望返回的值类型是：

```
{
    "status": 0,
    "message": "SUCCESS",
    "data": null
}
```

* status:状态码
* message:描述字段
* data:真实返回数据

> 外层是统一包装的，不可能每个控制器都手动加上这些；所以可以通过重构后置的处理程序来灵活的控制返回值的格式；

#### 1.看下HandlerMethodReturnValueHandler处理返回值程序接口

```
public interface HandlerMethodReturnValueHandler {

	boolean supportsReturnType(MethodParameter returnType);

	void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception;

}
```

上面的接口提供了两个方法：

* supportsReturnType：判断处理程序支持哪些返回值类型
* handleReturnValue：处理返回程序的具体方法

#### 2.自定义处理被@ResponseBody注解标注的方法处理程序

```
public class ResponseMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler proxyObject;

    public ResponseMethodReturnValueHandler(HandlerMethodReturnValueHandler proxyObject) {
        this.proxyObject = proxyObject;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class) ||
                returnType.hasMethodAnnotation(ResponseBody.class));
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest request) throws Exception {
        if(null != returnValue && (returnValue instanceof BaseResponse)){
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, request);
        } else {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put("status", 0);
            resultMap.put("message", "SUCCESS");
            resultMap.put("data", returnValue);
            proxyObject.handleReturnValue(resultMap, returnType, mavContainer, request);

        }
    }

}
```

上述处理程序可以支持返回值类型有：

* 字符串
* Map、List等容器集合
* 包装实体类

#### 3.自定义返回值类型是ResponsyEntity类型控制器处理程序

```
public class ResponseHttpEntityMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler proxyObject;

    public ResponseHttpEntityMethodReturnValueHandler(HandlerMethodReturnValueHandler proxyObject){
        this.proxyObject = proxyObject;
    }
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return (HttpEntity.class.isAssignableFrom(returnType.getParameterType()) &&
                !RequestEntity.class.isAssignableFrom(returnType.getParameterType()));
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest request) throws Exception {
        ResponseEntity entity = ((ResponseEntity) returnValue);
        Object body = entity.getBody();
        if(null != body && (body instanceof BaseResponse)){
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, request);
        } else {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put("status", 0);
            resultMap.put("message", "SUCCESS");
            resultMap.put("data", body);
            proxyObject.handleReturnValue(ResponseEntity.ok(resultMap), returnType, mavContainer, request);
        }
    }
}
```

上述处理程序支持通过ResponseEntity方式返回的值

#### 4.替换掉容器中对应的处理程序

```
/**
 *  控制器返回值配置处理类
 * @since 1.0
 */
@Configuration
public class ResponseMethodReturnValueConfigurer implements InitializingBean {

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @Override
    public void afterPropertiesSet()  {

        List<HandlerMethodReturnValueHandler> list = handlerAdapter.getReturnValueHandlers();
        if (null != list) {
            List<HandlerMethodReturnValueHandler> newList = new ArrayList<>();
            for (HandlerMethodReturnValueHandler valueHandler: list) {
                if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                    ResponseMethodReturnValueHandler proxy = new ResponseMethodReturnValueHandler(valueHandler);
                    newList.add(proxy);
                } else if(valueHandler instanceof HttpEntityMethodProcessor){
                    ResponseHttpEntityMethodReturnValueHandler proxy = new ResponseHttpEntityMethodReturnValueHandler(valueHandler);
                    newList.add(proxy);
                } else {
                    newList.add(valueHandler);
                }
            }
            handlerAdapter.setReturnValueHandlers(newList);
        }

    }
}
```

> spring boot框架已经定义了处理上述两种方式的处理程序，所以要做的就是将加载进入容器的处理程序替换成自定义的程序，这样就可以灵活的控制放回数据的格式；

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/doc/returnvalue](https://github.com/mingyang66/spring-parent/tree/master/doc/returnvalue)