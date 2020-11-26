package com.yaomy.control.test.consul;


import com.sgrain.boot.common.utils.path.PathMatcher;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/11/20
 */
public class TestDoubleColon {
    public static void main(String[] args) {
        PathMatcher pathMatcher = new PathMatcher(new String[]{"", "/", "/a/?", "/a/**"});
        System.out.println(pathMatcher.match(null));
        System.out.println(pathMatcher.match(""));
        System.out.println(pathMatcher.match("/"));
        System.out.println(pathMatcher.match("/a/a"));
        System.out.println(pathMatcher.match("/a/ab"));
        System.out.println(pathMatcher.match("/a/ab/a"));
    }
}
