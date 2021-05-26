package com.emily.infrastructure.autoconfigure.response.handler;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @Description: HttpHeader类型返回值处理程序
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
public class ResponseHttpHeadersReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler proxyObject;

    public ResponseHttpHeadersReturnValueHandler(HandlerMethodReturnValueHandler proxyObject) {
        this.proxyObject = proxyObject;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return HttpHeaders.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        //标注该请求已经在当前处理程序处理过
        mavContainer.setRequestHandled(true);
        if (returnValue instanceof HttpHeaders) {
            HttpHeaders headers = (HttpHeaders) returnValue;
            headers.add("token", "token");
            proxyObject.handleReturnValue(headers, returnType, mavContainer, webRequest);
        }
    }
}
