package com.yaomy.control.test.consul;


import com.sgrain.boot.common.utils.path.PathMatcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/11/20
 */
public class TestDoubleColon {
    public static void main(String[] args) {
        List<String> arrays = Arrays.asList("c", "d", "z", "0", "2", "100", "300", "123", "2", "a", "b", "z", "a");
        System.out.println(arrays);
        Collections.sort(arrays);
        System.out.println(arrays);
    }
}
