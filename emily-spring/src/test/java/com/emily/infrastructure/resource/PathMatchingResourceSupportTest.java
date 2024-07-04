package com.emily.infrastructure.resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/7/3 下午23:32
 */
public class PathMatchingResourceSupportTest {
    @Test
    public void getResource() throws IOException {
        final List<String> list = List.of("classpath*:mapper/mysql/*.xml", "classpath:mapper/oracle/*.xml", "classpath:*.properties");
        PathMatchingResourceSupport support = new PathMatchingResourceSupport();
        Assertions.assertThrows(FileNotFoundException.class, () -> support.getResources(list));

        final List<String> list1 = List.of("classpath:*-test.properties");
        List<Resource> resources = support.getResources(list1);
        Assertions.assertEquals(resources.size(), 2);
    }

    @Test
    public void getPathPattern() {
        PathMatchingResourceSupport support = new PathMatchingResourceSupport();
        // 是否是一个Ant模式字符串，即：是否包含通配符字符串
        Assertions.assertFalse(support.getPathMatcher().isPattern("/api/redis"));
        Assertions.assertFalse(support.getPathMatcher().isPattern("ap/redis/t-a"));
        Assertions.assertTrue(support.getPathMatcher().isPattern("ap/redis/*"));
        Assertions.assertTrue(support.getPathMatcher().isPattern("ap/redis/**/a/b"));
        Assertions.assertTrue(support.getPathMatcher().isPattern("ap/redis/a*"));
        Assertions.assertTrue(support.getPathMatcher().isPattern("ap/redis/a?"));
        Assertions.assertTrue(support.getPathMatcher().isPattern("ap/redis/{name}"));

        // url是否匹配指定的Ant模式
        Assertions.assertTrue(support.getPathMatcher().match("api/**/test","api/a/b/test"));
        Assertions.assertTrue(support.getPathMatcher().match("api/a*b/test","api/aAbcb/test"));
        Assertions.assertFalse(support.getPathMatcher().match("api/a*b/test","api/aAbcB/test"));
        Assertions.assertTrue(support.getPathMatcher().match("api/a?b/test","api/acb/test"));
        Assertions.assertFalse(support.getPathMatcher().match("api/a?b/test","api/acdb/test"));
        Assertions.assertTrue(support.getPathMatcher().match("api/{name}/test","api/acdb/test"));
        Assertions.assertFalse(support.getPathMatcher().match("api/a/**/b","api/a/bc"));

        // url是否匹配Ant模式，且以指定的模式开头
        Assertions.assertTrue(support.getPathMatcher().matchStart("api/{name}/test","api/acdb/test"));
        Assertions.assertTrue(support.getPathMatcher().matchStart("api/a/*","api/a/test"));
        Assertions.assertTrue(support.getPathMatcher().matchStart("api/a/*","api/a/bc"));
        Assertions.assertTrue(support.getPathMatcher().matchStart("api/a/**/b","api/a/bc"));
        Assertions.assertTrue(support.getPathMatcher().matchStart("api/a/**/b","api/a/bc/dd"));
        Assertions.assertTrue(support.getPathMatcher().matchStart("api/a/*/b","api/a/bc/b"));
        Assertions.assertFalse(support.getPathMatcher().matchStart("api/a/*/b","api/a/bc/d"));
        Assertions.assertTrue(support.getPathMatcher().matchStart("api/a/c*","api/a/cbc"));
        Assertions.assertFalse(support.getPathMatcher().matchStart("api/a/c*","api/a/c/dd"));

    }
}
