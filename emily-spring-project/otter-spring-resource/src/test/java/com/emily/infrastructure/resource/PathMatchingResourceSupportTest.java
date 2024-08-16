package com.emily.infrastructure.resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.util.PathMatcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author :  Emily
 * @since :  2024/7/3 下午23:32
 */
public class PathMatchingResourceSupportTest {
    @Test
    public void getResourcePattern() throws IOException {
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
        PathMatcher matcher = support.getPathMatcher();
        // 是否是一个Ant模式字符串，即：是否包含通配符字符串
        Assertions.assertFalse(matcher.isPattern("/api/redis"));
        Assertions.assertFalse(matcher.isPattern("ap/redis/t-a"));
        Assertions.assertTrue(matcher.isPattern("ap/redis/*"));
        Assertions.assertTrue(matcher.isPattern("ap/redis/**/a/b"));
        Assertions.assertTrue(matcher.isPattern("ap/redis/a*"));
        Assertions.assertTrue(matcher.isPattern("ap/redis/a?"));
        Assertions.assertTrue(matcher.isPattern("ap/redis/{name}"));

        // url是否匹配指定的Ant模式
        Assertions.assertTrue(matcher.match("api/**/test", "api/a/b/test"));
        Assertions.assertTrue(matcher.match("api/a*b/test", "api/aAbcb/test"));
        Assertions.assertFalse(matcher.match("api/a*b/test", "api/aAbcB/test"));
        Assertions.assertTrue(matcher.match("api/a?b/test", "api/acb/test"));
        Assertions.assertFalse(matcher.match("api/a?b/test", "api/acdb/test"));
        Assertions.assertTrue(matcher.match("api/{name}/test", "api/acdb/test"));
        Assertions.assertFalse(matcher.match("api/a/**/b", "api/a/bc"));

        // url是否匹配Ant模式，且以指定的模式开头
        Assertions.assertTrue(matcher.matchStart("api/{name}/test", "api/acdb/test"));
        Assertions.assertTrue(matcher.matchStart("api/a/*", "api/a/test"));
        Assertions.assertTrue(matcher.matchStart("api/a/*", "api/a/bc"));
        Assertions.assertTrue(matcher.matchStart("api/a/**/b", "api/a/bc"));
        Assertions.assertTrue(matcher.matchStart("api/a/**/b", "api/a/bc/dd"));
        Assertions.assertTrue(matcher.matchStart("api/a/*/b", "api/a/bc/b"));
        Assertions.assertFalse(matcher.matchStart("api/a/*/b", "api/a/bc/d"));
        Assertions.assertTrue(matcher.matchStart("api/a/c*", "api/a/cbc"));
        Assertions.assertFalse(matcher.matchStart("api/a/c*", "a/api/a/cbc"));
        Assertions.assertFalse(matcher.matchStart("api/a/c*", "api/a/c/dd"));

        // 提取Ant通配符匹配开始之后的路由，支持‘*’、‘**’、'?'
        Assertions.assertEquals(matcher.extractPathWithinPattern("/docs/cvs/commit.html", "/docs/cvs/commit.html"), "");
        Assertions.assertEquals(matcher.extractPathWithinPattern("/docs/*", "/docs/cvs/commit"), "cvs/commit");
        Assertions.assertEquals(matcher.extractPathWithinPattern("/docs/cvs/*.html", "/docs/cvs/commit.html"), "commit.html");
        Assertions.assertEquals(matcher.extractPathWithinPattern("/docs/**", "/docs/cvs/commit"), "cvs/commit");
        Assertions.assertEquals(matcher.extractPathWithinPattern("/docs/**\\/*.html", "/docs/cvs/commit.html"), "cvs/commit.html");
        Assertions.assertEquals(matcher.extractPathWithinPattern("/*.html", "/docs/cvs/commit.html"), "docs/cvs/commit.html");
        Assertions.assertEquals(matcher.extractPathWithinPattern("*.html", "/docs/cvs/commit.html"), "/docs/cvs/commit.html");
        Assertions.assertEquals(matcher.extractPathWithinPattern("*", "/docs/cvs/commit.html"), "/docs/cvs/commit.html");
        Assertions.assertEquals(matcher.extractPathWithinPattern("/a/b?", "/a/bc/d"), "bc/d");

        // 提取Ant风格url中变量
        Map<String, String> uriTemplate = matcher.extractUriTemplateVariables("api/{userid}/{id}", "api/1002356/11");
        Assertions.assertEquals(uriTemplate.get("userid"), "1002356");
        Assertions.assertEquals(uriTemplate.get("id"), "11");

        // 将两个URL拼接成一个新的url
        Assertions.assertEquals(matcher.combine(null, null), "");
        Assertions.assertEquals(matcher.combine("/hotels", null), "/hotels");
        Assertions.assertEquals(matcher.combine(null, "/hotels"), "/hotels");
        Assertions.assertEquals(matcher.combine("/hotels", "/bookings"), "/hotels/bookings");
        Assertions.assertEquals(matcher.combine("/hotels", "bookings"), "/hotels/bookings");
        Assertions.assertEquals(matcher.combine("/hotels/*", "bookings"), "/hotels/bookings");
        Assertions.assertEquals(matcher.combine("/hotels/**", "/bookings"), "/hotels/**/bookings");
        Assertions.assertEquals(matcher.combine("/hotels", "{hotel}"), "/hotels/{hotel}");
        Assertions.assertEquals(matcher.combine("/hotels/*", "{hotel}"), "/hotels/{hotel}");
        Assertions.assertEquals(matcher.combine("/hotels/**", "{hotel}"), "/hotels/**/{hotel}");
        Assertions.assertEquals(matcher.combine("/*.html", "/hotels.html"), "/hotels.html");
        Assertions.assertEquals(matcher.combine("/*.html", "/hotels"), "/hotels.html");
        Assertions.assertThrows(IllegalArgumentException.class, () -> matcher.combine("/*.html", "/*.txt"));

        // 给定一个url返回一个可以按照显式顺序排序的Comparator对象
        Comparator<String> comparator1 = matcher.getPatternComparator("/hotels/new");
        Comparator<String> comparator2 = matcher.getPatternComparator("/hotels/{hotel}");
        Comparator<String> comparator3 = matcher.getPatternComparator("/hotels/*");
    }

    @Test
    public void getResource() {
        PathMatchingResourceSupport support = new PathMatchingResourceSupport();
        // 支持类路径url
        Resource resource = support.getResource("classpath:resource-test.properties");
        Assertions.assertTrue(resource.exists());
        // 必须支持全限定路径
        //Resource resource1 = support.getResource("file:/Users/yaomingyang/Documents/IDE/workplace-java/spring-parent/emily-spring/src/test/resources/resource-test.properties");
        //Assertions.assertTrue(resource1.exists());
        // 支持相对路径
        Resource resource2 = support.getResource("WEB-INF/test.bat");
        Assertions.assertTrue(resource2.exists());
    }
}
