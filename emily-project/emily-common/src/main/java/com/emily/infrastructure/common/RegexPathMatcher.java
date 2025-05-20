package com.emily.infrastructure.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式匹配
 *
 * @author Emily
 * @since Created in 2023/7/2 1:13 PM
 */
public class RegexPathMatcher {
    private static final Map<String, Pattern> CACHE = new HashMap<>();

    /**
     * 判断给定的URL请求路径是否与指定的正则表达式匹配
     *
     * @param pattern 正则表达式
     * @param path    请求URL
     * @return true-请求路径符合正则表达式，false-请求路径不符合正则表达式
     */
    public static Matcher matcher(String pattern, String path) {
        if (!CACHE.containsKey(pattern)) {
            // 编译正则表达式
            CACHE.put(pattern, Pattern.compile(pattern));
        }
        // 匹配操作
        return CACHE.get(pattern).matcher(path);
    }

    /**
     * 判定给定的URL请求路径是否与指定的正则表达式配置
     *
     * @param patterns 正则表达式集合
     * @param path     请求URL
     * @return 如果有一个正则表达式匹配，则返回true；否则返回false
     */
    public static boolean matcherAny(List<String> patterns, String path) {
        for (String pattern : patterns) {
            if (matcher(pattern, path).matches()) {
                return true;
            }
        }
        return false;
    }
}
