package com.yaomy.handler.config;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: Description
 * @ProjectName: EM.FrontEnd.PrivateEquity.electronic-contract
 * @Package: com.uufund.ecapi.config.returnvalue.RestReturnValueHandler
 * @Author: å§šæ˜Žæ´‹
 * @Date: 2019/5/21 14:13
 * @Version: 1.0
 */
public class ResponseMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HandlerMethodReturnValueHandler proxyObject;

    public ResponseMethodReturnValueHandler(HandlerMethodReturnValueHandler proxyObject) {
        this.proxyObject = proxyObject;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return proxyObject.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest request) throws Exception {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("status", 0);
        resultMap.put("message", "SUCESS");
        resultMap.put("data", returnValue);
        proxyObject.handleReturnValue(resultMap, returnType, mavContainer, request);
    }

}
