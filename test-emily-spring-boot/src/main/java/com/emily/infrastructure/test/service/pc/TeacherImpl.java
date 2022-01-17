package com.emily.infrastructure.test.service.pc;

import org.springframework.stereotype.Service;

/**
 * @program: spring-parent
 * @description: l
 * @author: 姚明洋
 * @create: 2022/01/14
 */
@Service
public class TeacherImpl extends StudentImpl{

    @Override
    public String getName() {
        return "大米";
    }

    public String getTeacherName(){
        return "teacherName";
    }
}
