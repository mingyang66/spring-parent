package com.emily.infrastructure.core.helper;

import org.springframework.util.AntPathMatcher;

import java.util.Set;

/**
 * @Description: 处理匹配地址
 * @Author: Emily
 * @Date: 2019/11/13 13:39
 * @Version: 1.0
 */
public class AntPathUtils {

    public static final String STAR = "*";

    /**
     * 判定是否有符合条件的路由
     * 支持ant表达式
     * ?:匹配单个字符
     * *:匹配0或多个字符
     * **:匹配0或多个目录
     *
     * @param routes
     * @param route
     * @return
     */
    public static boolean match(Set<String> routes, String route) {
        AntPathMatcher matcher = new AntPathMatcher();
        if (routes.contains(STAR) || routes.contains(route)) {
            return true;
        }
        boolean isMatch = false;
        for (String excludeRoute : routes) {
            if (isMatch = matcher.match(excludeRoute, route)) {
                break;
            }
        }
        return isMatch;
    }
}
