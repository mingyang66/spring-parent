package com.sgrain.boot.autoconfigure.returnvalue.handler;

import com.sgrain.boot.autoconfigure.returnvalue.ReturnValueProperties;
import com.sgrain.boot.autoconfigure.returnvalue.annotation.ApiWrapperIgnore;
import com.sgrain.boot.common.base.BaseResponse;
import com.sgrain.boot.common.base.ResponseData;
import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.utils.path.PathMatcher;
import com.sgrain.boot.common.utils.path.PathUrls;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 控制器返回返回值包装类, 处理带@ResponseBody标识的返回值类型
 * @Version: 1.0
 */
public class ResponseMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler proxyObject;
    private ReturnValueProperties returnValueProperties;
    private PathMatcher pathMatcher;

    public ResponseMethodReturnValueHandler(HandlerMethodReturnValueHandler proxyObject, ReturnValueProperties returnValueProperties) {
        this.proxyObject = proxyObject;
        this.returnValueProperties = returnValueProperties;
        this.pathMatcher = new PathMatcher(ArrayUtils.addAll(this.returnValueProperties.getExclude().toArray(new String[]{}), PathUrls.defaultExcludeUrl));
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
            proxyObject.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            //返回值为void类型的data字段不输出
            if (returnType.getMethod().getReturnType().equals(Void.TYPE)) {
                ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.OK);
                proxyObject.handleReturnValue(responseData, returnType, mavContainer, webRequest);
            } else {
                BaseResponse baseResponse = BaseResponse.buildResponse(AppHttpStatus.OK);
                baseResponse.setData(returnValue);
                proxyObject.handleReturnValue(baseResponse, returnType, mavContainer, webRequest);
            }
        }
    }

}
