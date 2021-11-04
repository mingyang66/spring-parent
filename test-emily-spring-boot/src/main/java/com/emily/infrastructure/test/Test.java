package com.emily.infrastructure.test;

import java.time.Duration;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class Test {
    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException {
        Duration duration = Duration.ofMillis(10000);
        System.out.println(duration.toMillis());
        System.out.println(duration.toMillisPart());
    }

}
