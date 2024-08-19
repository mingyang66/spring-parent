package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.PathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * 路径规范化单元测试类
 *
 * @author Emily
 * @since Created in 2023/7/6 5:05 PM
 */
public class PathUtilsTest {
    @Test
    public void normalizePath() {
        Assertions.assertEquals(PathUtils.normalizePath(null), null);
        Assertions.assertEquals(PathUtils.normalizePath(""), "");
        Assertions.assertEquals(PathUtils.normalizePath("a"), "/a");
        Assertions.assertEquals(PathUtils.normalizePath("a/b"), "/a/b");
        Assertions.assertEquals(PathUtils.normalizePath("a/b/c/"), "/a/b/c");
        Assertions.assertEquals(PathUtils.normalizePath("/a/b/c/"), "/a/b/c");
    }
}
