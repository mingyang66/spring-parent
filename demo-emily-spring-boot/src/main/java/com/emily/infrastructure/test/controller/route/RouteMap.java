package com.emily.infrastructure.test.controller.route;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Description :  路由缓存
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/4 2:38 下午
 */
public class RouteMap {
    public static Map<String, String> CACHE = Maps.newHashMap();

    static {
        CACHE.put("/logback/debug", "/logback/debug1");
    }
}
