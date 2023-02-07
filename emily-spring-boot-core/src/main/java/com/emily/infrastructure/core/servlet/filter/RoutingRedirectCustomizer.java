package com.emily.infrastructure.core.servlet.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description :  路由重定向URL获取实现类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/4 11:14 上午
 */
public interface RoutingRedirectCustomizer {
    /**
     * 是否进行路由重定向
     *
     * @param request
     * @return
     */
    default boolean isRouteRedirect(HttpServletRequest request) {
        return false;
    }

    /**
     * 获取请求路由
     *
     * @param request
     * @return
     */
    default String resolveSpecifiedLookupPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
