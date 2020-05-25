package com.yaomy.control.test.api;

import java.util.*;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/03/06
 */
public class Test {
    public static void main(String[] args) {
        Optional<List<Map<String, Object>>> optional = Optional.ofNullable(Arrays.asList());
        System.out.println(optional.isPresent());
        System.out.println(Objects.isNull(null));
        System.out.println(Objects.compare(1, 3, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return 0;
            }
        }));
        String s = Objects.requireNonNull(null, "空指针异常浏览量");
    }
}
