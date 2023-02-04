package com.emily.infrastructure.autoconfigure.route.mapping;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @Description : 自定义RequestMappingHandlerMapping
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/2 9:32 上午
 */
public class LookupPathRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private LookupPathCustomizer requestMappingCustomizer;

    public LookupPathRequestMappingHandlerMapping() {

    }

    public LookupPathRequestMappingHandlerMapping(LookupPathCustomizer requestMappingCustomizer) {
        this.requestMappingCustomizer = requestMappingCustomizer;
    }

    /**
     * org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#getHandlerInternal(javax.servlet.http.HttpServletRequest)
     *
     * @param request
     * @return
     */
    @Override
    protected String initLookupPath(HttpServletRequest request) {
        String lookupPath = super.initLookupPath(request);
        request.setAttribute(RequestDispatcher.FORWARD_REQUEST_URI, request.getRequestURI());
        return this.requestMappingCustomizer == null ? lookupPath : this.requestMappingCustomizer.resolveSpecifiedLookupPath(lookupPath);
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info != null) {
            // 请求API路由添加前缀
            return RequestMappingInfo.paths().build().combine(info);
        }
        return null;
    }
}
