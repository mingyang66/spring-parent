package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.test.service.pc.TeacherImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description: 父子类接口拦截验证
 * @author: Emily
 * @create: 2022/01/14
 */
@RestController
@RequestMapping("api/pc")
public class ParentChildrenControler {
    @Autowired
    private TeacherImpl student;

    @GetMapping("test")
    public void test() {
        student.getName();
    }
}
