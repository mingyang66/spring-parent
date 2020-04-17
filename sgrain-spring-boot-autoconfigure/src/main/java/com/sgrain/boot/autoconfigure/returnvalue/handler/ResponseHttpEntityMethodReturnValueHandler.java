package com.sgrain.boot.autoconfigure.returnvalue.handler;

import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.po.ResponseData;
import com.sgrain.boot.common.utils.RouteUtils;
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
        } else if(null != body && (body instanceof ResponseData)){
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put("status", AppHttpStatus.OK.getStatus());
            resultMap.put("message", AppHttpStatus.OK.getMessage());
            //获取控制器方法返回值得泛型类型
            Type type = returnType.getMethod().getGenericReturnType();
            //返回值为void类型的data字段不输出
            if((type instanceof ParameterizedType) && !(((ParameterizedType)type).getActualTypeArguments()[0]).equals(Void.class)){
                resultMap.put("data", body);
            }
            proxyObject.handleReturnValue(ResponseEntity.ok(resultMap), returnType, mavContainer, webRequest);
        }
    }
}
