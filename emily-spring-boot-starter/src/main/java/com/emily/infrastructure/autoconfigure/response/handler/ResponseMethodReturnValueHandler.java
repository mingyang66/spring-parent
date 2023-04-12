package com.emily.infrastructure.autoconfigure.response.handler;

import com.emily.infrastructure.autoconfigure.response.ResponseWrapperProperties;
import com.emily.infrastructure.autoconfigure.response.annotation.ApiWrapperIgnore;
import com.emily.infrastructure.common.entity.BaseResponse;
import com.emily.infrastructure.common.type.HttpStatusType;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.common.utils.path.PathMatcher;
import com.emily.infrastructure.common.utils.path.PathUrls;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Emily
 * @Description: 控制器返回返回值包装类, 处理带@ResponseBody标识的返回值类型
 * @Version: 1.0
 */
public class ResponseMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler proxyObject;
    private ResponseWrapperProperties returnValueProperties;
    private PathMatcher pathMatcher;

    public ResponseMethodReturnValueHandler(HandlerMethodReturnValueHandler proxyObject, ResponseWrapperProperties returnValueProperties) {
        this.proxyObject = proxyObject;
        this.returnValueProperties = returnValueProperties;
        this.pathMatcher = new PathMatcher(ArrayUtils.addAll(this.returnValueProperties.getExclude().toArray(new String[]{}), PathUrls.DEFAULT_EXCLUDE_URL));
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
        if (returnType.hasMethodAnnotation(ApiWrapperIgnore.class)
                || returnType.getContainingClass().isAnnotationPresent(ApiWrapperIgnore.class)
                || pathMatcher.match(request.getRequestURI())) {
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else if (null != returnValue && (returnValue instanceof BaseResponse)) {
            BaseResponse baseResponse = (BaseResponse) returnValue;
            baseResponse.setSpentTime(RequestUtils.getSpentTime());
            proxyObject.handleReturnValue(baseResponse, returnType, mavContainer, webRequest);
        } else {
            //返回值为void类型的data字段不输出
            if (returnType.getMethod().getReturnType().equals(Void.TYPE)) {
                BaseResponse baseResponse = BaseResponse.buildResponse(HttpStatusType.OK);
                proxyObject.handleReturnValue(baseResponse, returnType, mavContainer, webRequest);
            } else {
                BaseResponse baseResponse = BaseResponse.buildResponse(HttpStatusType.OK, returnValue);
                proxyObject.handleReturnValue(baseResponse, returnType, mavContainer, webRequest);
            }
        }
    }

}
