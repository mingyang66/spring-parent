package com.emily.infrastructure.common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description :  正则表达式匹配
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/2 1:13 PM
 */
public class RegexPathMatcher {
    /**
     * 判断给定的URL请求路径是否与指定的正则表达式匹配
     *
     * @param pattern 正则表达式
     * @param path    请求URL
     * @return true-请求路径符合正则表达式，false-请求路径不符合正则表达式
     */
    public static Matcher matcher(String pattern, String path) {
        // 编译正则表达式
        Pattern p = Pattern.compile(pattern);
        // 匹配操作
        return p.matcher(path);
    }

    /**
     * 判定给定的URL请求路径是否与指定的正则表达式配置
     * @param patterns
     * @param path
     * @return
     */
    public static boolean matcherAny(List<String> patterns, String path) {
        for (int i = 0; i < patterns.size(); i++) {
            if (matcher(patterns.get(i), path).matches()) {
                return true;
            }
        }
        return false;
    }
}
