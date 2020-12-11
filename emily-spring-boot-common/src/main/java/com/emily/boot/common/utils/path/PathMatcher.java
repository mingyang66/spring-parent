package com.emily.boot.common.utils.path;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.*;

/**
 * @program: spring-parent
 * @description: 路径判断是否符合规则
 * @create: 2020/11/26
 */
public class PathMatcher {

    private PathPatterns pathPatterns;

    public PathMatcher(String... defaultPatterns) {
        this.pathPatterns = new PathPatterns(defaultPatterns);
    }

    /**
     * 判定给定的路径是否符合条件
     * 支持 * 号匹配所有
     * 支持ant表达式
     * ?:匹配单个字符
     * *:匹配0或多个字符
     * **:匹配0或多个目录
     *
     * @param path
     * @return
     */
    public boolean match(String path) {
        return pathPatterns.matchesAll || pathPatterns.matches(path);
    }

    /**
     * 判定URL是否以指定的前缀开头
     *
     * @param prefix 前缀
     * @param path   URL
     * @return
     */
    public boolean matchStart(String prefix, String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        return path.startsWith(prefix);
    }

    /**
     * 判定给定的路径是否全部符合条件
     * 支持 * 号匹配所有
     * 支持ant表达式
     * ?:匹配单个字符
     * *:匹配0或多个字符
     * **:匹配0或多个目录
     *
     * @param path
     * @return
     */
    public boolean matchAll(String... path) {
        if (pathPatterns.matchesAll) {
            return true;
        }
        for (int i = 0; i < path.length; i++) {
            if (!pathPatterns.matches(path[i])) {
                return false;
            }
        }
        return true;
    }

    private static class PathPatterns {
        /**
         * Ant 表达式匹配类
         */
        private final AntPathMatcher matcher = new AntPathMatcher();

        private final boolean matchesAll;

        private final Set<String> paths;

        PathPatterns(String[] patterns) {
            this((patterns != null) ? Arrays.asList(patterns) : (Collection<String>) null);
        }

        PathPatterns(Collection<String> patterns) {
            patterns = (patterns != null) ? patterns : Collections.emptySet();
            boolean matchesAll = false;
            Set<String> paths = new LinkedHashSet<>();
            for (String pattern : patterns) {
                if ("*".equals(pattern)) {
                    matchesAll = true;
                } else if(StringUtils.isNotEmpty(pattern)){
                    paths.add(pattern);
                }
            }
            this.matchesAll = matchesAll;
            this.paths = paths;
        }

        boolean matches(String path) {
            return this.matchesAll || this.paths.contains(path) || this.antMatches(path);
        }

        /**
         * @param path 支持ant表达式
         *             ?:匹配单个字符
         *             *:匹配0或多个字符
         *             **:匹配0或多个目录
         * @return
         */
        boolean antMatches(String path) {
            for (Iterator<String> it = this.paths.iterator(); it.hasNext(); ) {
                if (this.matcher.match(it.next(), path)) {
                    return true;
                }
            }
            return false;
        }
    }

}
