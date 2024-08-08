package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.RegexPathMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 *  正则表达式匹配单元测试
 * @author  Emily
 * @since  Created in 2023/7/2 1:19 PM
 */
public class RegexPathMatcherTest {
    @Test
    public void match() {
        String pattern = "^/swagger-resources/.*$";
        Assertions.assertEquals(RegexPathMatcher.matcher(pattern, "/swagger-resources/ba.html").matches(), true);
        Assertions.assertEquals(RegexPathMatcher.matcher(pattern, "1/swagger-resources/ba.html").matches(), false);
        Assertions.assertEquals(RegexPathMatcher.matcher(pattern, "/swagger-resources/").matches(), true);
        Assertions.assertEquals(RegexPathMatcher.matcher("/v2/api-docs", "/v2/api-docs").matches(), true);
        Assertions.assertEquals(RegexPathMatcher.matcher("/swagger-ui.html", "/swagger-ui.html").matches(), true);
        Assertions.assertEquals(RegexPathMatcher.matcher("/error", "/error").matches(), true);

        Matcher matcher = RegexPathMatcher.matcher("^(A|B|C) TO (D|E|F)$", "B TO E");
        Assertions.assertEquals(matcher.find(), true);
        Assertions.assertEquals(matcher.group(), "B TO E");
        Assertions.assertEquals(matcher.groupCount(), 2);
        Assertions.assertEquals(matcher.group(0), "B TO E");
        Assertions.assertEquals(matcher.group(1), "B");
        Assertions.assertEquals(matcher.group(2), "E");
    }

    @Test
    public void matchAny() {
        List<String> list = new ArrayList<>();
        list.add("^/swagger-resources/.*$");
        list.add("/v2/api-docs");
        list.add("/swagger-ui.html");
        list.add("/error");
        Assertions.assertEquals(RegexPathMatcher.matcherAny(list, "/swagger-resources/bc"), true);
        Assertions.assertEquals(RegexPathMatcher.matcherAny(list, "bb/swagger-resources/bc"), false);
        Assertions.assertEquals(RegexPathMatcher.matcherAny(list, "/swagger-ui.html"), true);
        Assertions.assertEquals(RegexPathMatcher.matcherAny(list, "b/swagger-ui.html"), false);
    }
}
