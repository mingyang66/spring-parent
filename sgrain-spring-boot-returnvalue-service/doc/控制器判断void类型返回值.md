### 控制器判断void类型返回值

##### 1.返回值类型为void类型

```
 if(!returnType.getMethod().getReturnType().equals(Void.TYPE)){
           //TODO
   }
```

##### 2.返回值类型为ResponseEntity类型

```
 Type type = returnType.getMethod().getGenericReturnType();
            if((type instanceof ParameterizedType) && !(((ParameterizedType)type).getActualTypeArguments()[0]).equals(Void.class)){
               //TODO
            }
```

##### 3.具体示例

```
package com.yaomy.control.returnvalue.handler;

import BaseResponse;
import RouteUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 控制器返回返回值包装类,处理带@ResponseBody标识的返回值类型
 * @Version: 1.0
 */
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
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        //标注该请求已经在当前处理程序处理过
        mavContainer.setRequestHandled(true);
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if(RouteUtils.readRoute().contains(request.getRequestURI())){
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else if(null != returnValue && (returnValue instanceof BaseResponse)){
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put("status", 0);
            resultMap.put("message", "SUCCESS");
            if(!returnType.getMethod().getReturnType().equals(Void.TYPE)){
                resultMap.put("data", returnValue);
            }
            proxyObject.handleReturnValue(resultMap, returnType, mavContainer, webRequest);
        }
    }

}

```

示例二：

```
package com.yaomy.control.returnvalue.handler;

import BaseResponse;
import RouteUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: HttpEntity返回值控制
 * @Version: 1.0
 */
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
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        //标注该请求已经在当前处理程序处理过
        mavContainer.setRequestHandled(true);
        //获取ResponseEntity封装的真实返回值
        Object body = (null == returnValue) ? null :((ResponseEntity) returnValue).getBody();
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if(RouteUtils.readRoute().contains(request.getRequestURI())){
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else if(null != body && (body instanceof BaseResponse)){
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put("status", 0);
            resultMap.put("message", "SUCCESS");
            Type type = returnType.getMethod().getGenericReturnType();
            if((type instanceof ParameterizedType) && !(((ParameterizedType)type).getActualTypeArguments()[0]).equals(Void.class)){
                resultMap.put("data", body);
            }
            proxyObject.handleReturnValue(ResponseEntity.ok(resultMap), returnType, mavContainer, webRequest);
        }
    }
}

```

GitHub地址：(https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-returnvalue-service/doc)[https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-returnvalue-service/doc]
