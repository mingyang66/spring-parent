package com.emily.infrastructure.test.service.pc;

/**
 * @program: spring-parent
 * @description: 学生实现类
 * @author: Emily
 * @create: 2022/01/14
 */
public class StudentImpl implements Student {


    @Override
    public String getName() {
        return "小米";
    }

    @Override
    public String getPassword() {
        return "鸿蒙";
    }
}
