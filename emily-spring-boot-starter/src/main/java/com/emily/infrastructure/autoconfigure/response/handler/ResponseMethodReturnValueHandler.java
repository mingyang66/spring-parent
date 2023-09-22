package com.emily.infrastructure.autoconfigure.response.handler;

import com.emily.infrastructure.autoconfigure.response.ResponseWrapperProperties;
import com.emily.infrastructure.autoconfigure.response.annotation.ApiResponseWrapperIgnore;
import com.emily.infrastructure.common.RegexPathMatcher;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.core.entity.BaseResponseBuilder;
import com.emily.infrastructure.core.exception.HttpStatusType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * 控制器返回返回值包装类, 处理带@ResponseBody标识的返回值类型
 *
 * @author Emily
 * @since 1.0
 */
public class ResponseMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler proxyObject;
    private ResponseWrapperProperties properties;
    //private PathMatcher pathMatcher;

    public ResponseMethodReturnValueHandler(HandlerMethodReturnValueHandler proxyObject, ResponseWrapperProperties properties) {
        this.proxyObject = proxyObject;
        this.properties = properties;
        //this.pathMatcher = new PathMatcher(ArrayUtils.addAll(this.returnValueProperties.getExclude().toArray(new String[]{}), PathUrls.DEFAULT_EXCLUDE_URL));
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
        if (returnType.hasMethodAnnotation(ApiResponseWrapperIgnore.class)
                || returnType.getContainingClass().isAnnotationPresent(ApiResponseWrapperIgnore.class)
                || RegexPathMatcher.matcherAny(properties.getExclude(), request.getRequestURI())) {
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else if (null != returnValue && (returnValue instanceof BaseResponse)) {
            BaseResponse baseResponse = (BaseResponse) returnValue;
            baseResponse.setSpentTime(LocalContextHolder.current().getSpentTime());
            proxyObject.handleReturnValue(baseResponse, returnType, mavContainer, webRequest);
        } else {
            //返回值为void类型的data字段不输出
            if (returnType.getMethod().getReturnType().equals(Void.TYPE)) {
                BaseResponse baseResponse = new BaseResponseBuilder<>().withStatus(HttpStatusType.OK.getStatus()).withMessage(HttpStatusType.OK.getMessage()).build();
                proxyObject.handleReturnValue(baseResponse, returnType, mavContainer, webRequest);
            } else {
                BaseResponse baseResponse = new BaseResponseBuilder<>().withStatus(HttpStatusType.OK.getStatus()).withMessage(HttpStatusType.OK.getMessage()).withData(returnValue).build();
                proxyObject.handleReturnValue(baseResponse, returnType, mavContainer, webRequest);
            }
        }
    }

}
