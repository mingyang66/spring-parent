package com.emily.infrastructure.test.mainTest;

import com.emily.infrastructure.common.utils.hash.DesUtils;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2022/01/11
 */
public class DesMain {
    public static void main(String[] args) {
        String s = DesUtils.encrypt("87654321", "10000098");
        System.out.println(s);
        String s1 = DesUtils.decrypt("87654321", s);
        System.out.println(s1);
        System.out.println(DesUtils.decrypt("eastabcd", "er2bqAWRlEK7X7oyl5aoRA=="));
    }
}
