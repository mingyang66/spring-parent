package com.emily.infrastructure.common.utils.path;

import org.springframework.util.StringUtils;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 路径工具类
 * @create: 2020/11/26
 */
public class PathUtils {
    /**
     * @Description 规范化路径
     * @param path 路径
     * @return
     */
    public static String normalizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return path;
        }
        String normalizedPath = path;
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return normalizedPath;
    }
}
