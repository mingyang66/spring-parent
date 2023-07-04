package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.RegexPathMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @Description :  正则表达式匹配单元测试
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/2 1:19 PM
 */
public class RegexPathMatcherTest {
    @Test
    public void match() {
        String pattern = "^/swagger-resources/.*$";
        Assert.assertEquals(RegexPathMatcher.matcher(pattern, "/swagger-resources/ba.html").matches(), true);
        Assert.assertEquals(RegexPathMatcher.matcher(pattern, "1/swagger-resources/ba.html").matches(), false);
        Assert.assertEquals(RegexPathMatcher.matcher(pattern, "/swagger-resources/").matches(), true);
        Assert.assertEquals(RegexPathMatcher.matcher("/v2/api-docs", "/v2/api-docs").matches(), true);
        Assert.assertEquals(RegexPathMatcher.matcher("/swagger-ui.html", "/swagger-ui.html").matches(), true);
        Assert.assertEquals(RegexPathMatcher.matcher("/error", "/error").matches(), true);

        Matcher matcher = RegexPathMatcher.matcher("^(A|B|C) TO (D|E|F)$", "B TO E");
        Assert.assertEquals(matcher.find(), true);
        Assert.assertEquals(matcher.group(), "B TO E");
        Assert.assertEquals(matcher.groupCount(), 2);
        Assert.assertEquals(matcher.group(0), "B TO E");
        Assert.assertEquals(matcher.group(1), "B");
        Assert.assertEquals(matcher.group(2), "E");
    }

    @Test
    public void matchAny() {
        List<String> list = new ArrayList<>();
        list.add("^/swagger-resources/.*$");
        list.add("/v2/api-docs");
        list.add("/swagger-ui.html");
        list.add("/error");
        Assert.assertEquals(RegexPathMatcher.matcherAny(list, "/swagger-resources/bc"), true);
        Assert.assertEquals(RegexPathMatcher.matcherAny(list, "bb/swagger-resources/bc"), false);
        Assert.assertEquals(RegexPathMatcher.matcherAny(list, "/swagger-ui.html"), true);
        Assert.assertEquals(RegexPathMatcher.matcherAny(list, "b/swagger-ui.html"), false);
    }
}
