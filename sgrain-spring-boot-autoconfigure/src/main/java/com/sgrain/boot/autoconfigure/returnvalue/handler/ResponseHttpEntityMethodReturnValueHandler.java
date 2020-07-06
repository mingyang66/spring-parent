package com.sgrain.boot.autoconfigure.returnvalue.handler;

import com.sgrain.boot.autoconfigure.returnvalue.annotation.ApiWrapperIgnore;
import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.po.BaseResponse;
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

/**
 * @Description: HttpEntity返回值控制
 * @Version: 1.0
 */
public class ResponseHttpEntityMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler proxyObject;

    public ResponseHttpEntityMethodReturnValueHandler(HandlerMethodReturnValueHandler proxyObject) {
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
        Object body = (null == returnValue) ? null : ((ResponseEntity) returnValue).getBody();
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (RouteUtils.readRoute().contains(request.getRequestURI())
                || returnType.hasMethodAnnotation(ApiWrapperIgnore.class)
                || returnType.getContainingClass().isAnnotationPresent(ApiWrapperIgnore.class)) {
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else if (null != body && (body instanceof BaseResponse)) {
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            //获取控制器方法返回值得泛型类型
            Type type = returnType.getMethod().getGenericReturnType();
            /**
             * 1.如果返回的是ResponseEntity类，无泛型化参数
             * 2.返回的ResponseEntity带泛型化参数，且参数是void
             */
            if ((type.equals(ResponseEntity.class)) || ((type instanceof ParameterizedType) && (((ParameterizedType) type).getActualTypeArguments()[0]).equals(Void.class))) {
                ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.OK);
                proxyObject.handleReturnValue(ResponseEntity.ok(responseData), returnType, mavContainer, webRequest);
            } else {
                BaseResponse baseResponse = BaseResponse.buildResponse(AppHttpStatus.OK);
                baseResponse.setData(body);
                proxyObject.handleReturnValue(ResponseEntity.ok(baseResponse), returnType, mavContainer, webRequest);
            }
        }
    }
}
