package com.emily.infrastructure.test.controller.route;

import com.emily.infrastructure.core.servlet.filter.RoutingRedirectCustomizer;

/**
 * @Description :  默认路由跳转实现
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/4 1:13 下午
 */
public class DefaultRoutingRedirectCustomizer implements RoutingRedirectCustomizer {
    @Override
    public boolean containsLookupPath(String lookupPath) {
        if (RouteMap.CACHE.containsKey(lookupPath)) {
            return true;
        }
        return false;
    }

    @Override
    public String resolveSpecifiedLookupPath(String lookupPath) {
        return RouteMap.CACHE.get(lookupPath);
    }
}
