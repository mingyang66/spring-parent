package com.emily.infrastructure.autoconfigure.response.handler;

import com.emily.infrastructure.autoconfigure.response.ResponseWrapperProperties;
import com.emily.infrastructure.autoconfigure.response.annotation.ApiResponseWrapperIgnore;
import com.emily.infrastructure.common.RegexPathMatcher;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.core.entity.BaseResponseBuilder;
import com.emily.infrastructure.core.exception.HttpStatusType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * HttpEntity返回值控制
 *
 * @author Emily
 * @since 1.0
 */
public class ResponseHttpEntityMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler proxyObject;
    private ResponseWrapperProperties properties;

    public ResponseHttpEntityMethodReturnValueHandler(HandlerMethodReturnValueHandler proxyObject, ResponseWrapperProperties properties) {
        this.proxyObject = proxyObject;
        this.properties = properties;
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
        ResponseEntity entity = (ResponseEntity) returnValue;
        //获取ResponseEntity封装的真实返回值
        Object body = (null == returnValue) ? null : entity.getBody();
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (entity.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
            String path = ((Map) body).get("path").toString();
            BaseResponse baseResponse = new BaseResponseBuilder<>().withStatus(HttpStatus.NOT_FOUND.value()).withMessage(StringUtils.join("接口【", path, "】不存在")).build();
            proxyObject.handleReturnValue(ResponseEntity.ok(baseResponse), returnType, mavContainer, webRequest);
        } else if (returnType.hasMethodAnnotation(ApiResponseWrapperIgnore.class)
                || returnType.getContainingClass().isAnnotationPresent(ApiResponseWrapperIgnore.class)
                || RegexPathMatcher.matcherAny(properties.getExclude(), request.getRequestURI())) {
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else if (null != body && (body instanceof BaseResponse)) {
            BaseResponse baseResponse = (BaseResponse) body;
            baseResponse.setSpentTime(LocalContextHolder.current().getSpentTime());
            proxyObject.handleReturnValue(baseResponse, returnType, mavContainer, webRequest);
        } else {
            //获取控制器方法返回值得泛型类型
            Type type = returnType.getMethod().getGenericReturnType();
            /**
             * 1.如果返回的是ResponseEntity类，无泛型化参数
             * 2.返回的ResponseEntity带泛型化参数，且参数是void
             */
            boolean flag = (type.equals(ResponseEntity.class)) || ((type instanceof ParameterizedType) && (((ParameterizedType) type).getActualTypeArguments()[0]).equals(Void.class));
            if (flag) {
                BaseResponse baseResponse = new BaseResponseBuilder<>().withStatus(HttpStatusType.OK.getStatus()).withMessage(HttpStatusType.OK.getMessage()).build();
                proxyObject.handleReturnValue(ResponseEntity.ok(baseResponse), returnType, mavContainer, webRequest);
            } else {
                BaseResponse baseResponse = new BaseResponseBuilder<>().withStatus(HttpStatusType.OK.getStatus()).withMessage(HttpStatusType.OK.getMessage()).withData(body).build();
                proxyObject.handleReturnValue(ResponseEntity.ok(baseResponse), returnType, mavContainer, webRequest);
            }
        }
    }
}
