package com.emily.infrastructure.logger.common;


/**
 * @author Emily
 * @program: spring-parent
 * @description: 路径工具类
 * @create: 2020/11/26
 */
public class PathUtils {

    public static final String SLASH = "/";
    public static final String DOT = ".";

    /**
     * @param path 路径
     * @return
     * @Description 规范化路径
     */
    public static String normalizePath(String path) {
        if (path == null || path.length() == 0) {
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
