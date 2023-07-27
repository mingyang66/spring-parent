package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.PathUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *  路径规范化单元测试类
 * @author  Emily
 * @since  Created in 2023/7/6 5:05 PM
 */
public class PathUtilsTest {
    @Test
    public void normalizePath() {
        Assert.assertEquals(PathUtils.normalizePath(null), null);
        Assert.assertEquals(PathUtils.normalizePath(""), "");
        Assert.assertEquals(PathUtils.normalizePath("a"), "/a");
        Assert.assertEquals(PathUtils.normalizePath("a/b"), "/a/b");
        Assert.assertEquals(PathUtils.normalizePath("a/b/c/"), "/a/b/c");
        Assert.assertEquals(PathUtils.normalizePath("/a/b/c/"), "/a/b/c");
    }
}
