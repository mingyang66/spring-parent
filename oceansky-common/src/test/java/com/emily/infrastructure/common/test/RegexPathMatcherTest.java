package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.RegexPathMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description :  正则表达式匹配单元测试
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/2 1:19 PM
 */
public class RegexPathMatcherTest {
    @Test
    public void match() {
        String pattern = "^/swagger-resources/.*$";
        Assert.assertEquals(RegexPathMatcher.match(pattern, "/swagger-resources/ba.html"), true);
        Assert.assertEquals(RegexPathMatcher.match(pattern, "1/swagger-resources/ba.html"), false);
        Assert.assertEquals(RegexPathMatcher.match("/v2/api-docs", "/v2/api-docs"), true);
        Assert.assertEquals(RegexPathMatcher.match("/swagger-ui.html", "/swagger-ui.html"), true);
        Assert.assertEquals(RegexPathMatcher.match("/error", "/error"), true);
    }

    @Test
    public void matchAny() {
        List<String> list = new ArrayList<>();
        list.add("^/swagger-resources/.*$");
        list.add("/v2/api-docs");
        list.add("/swagger-ui.html");
        list.add("/error");
        Assert.assertEquals(RegexPathMatcher.matchAny(list, "/swagger-resources/bc"), true);
        Assert.assertEquals(RegexPathMatcher.matchAny(list, "bb/swagger-resources/bc"), false);
        Assert.assertEquals(RegexPathMatcher.matchAny(list, "/swagger-ui.html"), true);
        Assert.assertEquals(RegexPathMatcher.matchAny(list, "b/swagger-ui.html"), false);
    }
}
