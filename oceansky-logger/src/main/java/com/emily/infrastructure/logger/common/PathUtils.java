package com.emily.infrastructure.logger.common;


import org.apache.commons.lang3.StringUtils;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 路径工具类
 * @create: 2020/11/26
 */
public class PathUtils {

    private static final String SLASH = "/";

    /**
     * @param path 路径
     * @return
     * @Description 规范化路径
     */
    public static String normalizePath(String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        String normalizedPath = path;
        if (!normalizedPath.startsWith(SLASH)) {
            normalizedPath = SLASH + normalizedPath;
        }
        if (normalizedPath.endsWith(SLASH)) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return normalizedPath;
    }
}
