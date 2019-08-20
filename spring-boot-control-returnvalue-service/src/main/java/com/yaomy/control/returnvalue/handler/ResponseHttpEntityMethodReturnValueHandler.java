package com.yaomy.control.returnvalue.handler;

import com.yaomy.control.common.control.po.BaseResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

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
