package com.emily.infrastructure.logger.common;


/**
 * @author Emily
 * <p>
 * 路径工具类
 * @since : 2020/11/26
 */
public class PathUtils {

    public static final String SLASH = "/";
    public static final String DOT = ".";

    /**
     * 路径格式化
     * --------------------------------------------------
     * 示例：
     * Assert.assertEquals(PathUtils.normalizePath(null), "");
     * Assert.assertEquals(PathUtils.normalizePath(""), "");
     * Assert.assertEquals(PathUtils.normalizePath("a/"), "/a");
     * Assert.assertEquals(PathUtils.normalizePath("/a/"), "/a");
     * Assert.assertEquals(PathUtils.normalizePath("/a/b"), "/a/b");
     * Assert.assertEquals(PathUtils.normalizePath("/a/b/"), "/a/b");
     * --------------------------------------------------
     * <p>
     * 规范化路径
     *
     * @param path 路径
     * @return 格式化后的url
     */
    public static String normalizePath(String path) {
        if (path == null || path.length() == 0) {
            return StrUtils.EMPTY;
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
