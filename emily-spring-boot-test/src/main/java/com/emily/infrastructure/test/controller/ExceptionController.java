package com.emily.infrastructure.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description: 异常控制器
 * @author: Emily
 * @create: 2021/08/21
 */
@RestController
@RequestMapping("/api/exception")
public class ExceptionController {

    @GetMapping("null")
    public void exception(){
        String s = null;
        s.length();
    }
}
