package com.emily.infrastructure.common;


/**
 * @author Emily
 * @program: spring-parent
 * @description: 路径工具类
 * @create: 2020/11/26
 */
public class PathUtils {

    private static final String SLASH = "/";

    /**
     * -------------------------------------------------------
     * 案例说明：
     * Assert.assertEquals(PathUtils.normalizePath(null), null);
     * Assert.assertEquals(PathUtils.normalizePath(""), "");
     * Assert.assertEquals(PathUtils.normalizePath("a"), "/a");
     * Assert.assertEquals(PathUtils.normalizePath("a/b"), "/a/b");
     * Assert.assertEquals(PathUtils.normalizePath("a/b/c/"), "/a/b/c");
     * Assert.assertEquals(PathUtils.normalizePath("/a/b/c/"), "/a/b/c");
     * -------------------------------------------------------
     *
     * @param path 路径格式规范化
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
