package com.emily.infrastructure.test;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/29 5:21 PM
 */
public class Test {
    public static void main(String[] args) {
        Map<String, String> d = Maps.newHashMap();
        String s = d.get("a");
        System.out.println(s);
    }
}
