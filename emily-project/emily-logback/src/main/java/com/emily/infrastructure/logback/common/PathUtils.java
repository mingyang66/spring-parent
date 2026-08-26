package com.emily.infrastructure.logback.common;


/**
 * @author Emily
 * <p>
 * 路径工具类
 * @since : 2020/11/26
 */
public final class PathUtils {

    public static final String SLASH = "/";
    public static final String DOT = ".";

    private PathUtils() {
    }

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
     * @return 以单个斜杠开头且不以斜杠结尾的逻辑路径
     */
    public static String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return StrUtils.EMPTY;
        }
        String normalizedPath = path.replace('\\', '/').replaceAll("/+", SLASH);
        normalizedPath = normalizedPath.replaceAll("^/+|/+$", StrUtils.EMPTY);
        return normalizedPath.isEmpty() ? StrUtils.EMPTY : SLASH + normalizedPath;
    }
}
