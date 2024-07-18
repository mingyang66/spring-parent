package com.emily.infrastructure.test.config.spi;

/**
 * @author Emily
 * @program: spring-parent
 * 学生
 * @since 2021/11/30
 */
public class Student implements People {

    public String getName() {
        return "中学生";
    }
}
