package com.yaomy.control.test.api;

import com.google.common.collect.Lists;
import com.sgrain.boot.common.utils.json.JSONUtils;

import java.util.*;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/03/06
 */
public class Test {
    public static void main(String[] args) {
        List<Integer> list = Lists.newArrayList(3,6,2,9,1);
        System.out.println(JSONUtils.toJSONString(list));
    }
}
