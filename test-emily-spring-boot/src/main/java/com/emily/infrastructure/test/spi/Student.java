package com.emily.infrastructure.test.spi;

/**
 * @program: spring-parent
 * @description: 学生
 * @author: Emily
 * @create: 2021/11/30
 */
public class Student implements People{
    @Override
    public String getName() {
        return "中学生";
    }
}
