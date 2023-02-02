package com.emily.infrastructure.autoconfigure.route.mapping;

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;

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
        return this.requestMappingCustomizer == null ? lookupPath : this.requestMappingCustomizer.resolveSpecifiedLookupPath(lookupPath);
    }
}
