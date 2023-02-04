package com.emily.infrastructure.core.servlet.filter;

/**
 * @Description :  路由重定向URL获取实现类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/4 11:14 上午
 */
public interface RoutingRedirectCustomizer {
    /**
     * 是否包含指定的路由key
     *
     * @param lookupPath 原始key
     * @return
     */
    default boolean containsLookupPath(String lookupPath) {
        return false;
    }

    /**
     * 获取请求路由
     *
     * @param lookupPath 原始请求路由
     * @return
     */
    default String resolveSpecifiedLookupPath(String lookupPath) {
        return lookupPath;
    }
}
