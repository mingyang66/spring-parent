package com.yaomy.control.returnvalue.handler;

import com.yaomy.control.common.control.po.BaseResponse;
import com.yaomy.control.common.control.utils.SwaggerUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
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
        ResponseEntity entity = ((ResponseEntity) returnValue);
        Object body = entity.getBody();
        //标注该请求已经在当前处理程序处理过
        mavContainer.setRequestHandled(true);
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if(SwaggerUtils.urls.contains(request.getRequestURI())){
            proxyObject.handleReturnValue(entity, returnType, mavContainer, webRequest);
        } else if(null != body && (body instanceof BaseResponse)){
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            resultMap.put("status", 0);
            resultMap.put("message", "SUCCESS");
            resultMap.put("data", body);
            proxyObject.handleReturnValue(ResponseEntity.ok(resultMap), returnType, mavContainer, webRequest);
        }
    }
}
