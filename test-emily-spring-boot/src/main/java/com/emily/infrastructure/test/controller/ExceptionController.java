package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.test.exception.ApiException;
import com.emily.infrastructure.test.po.Job;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: spring-parent
 * @description: 异常控制器
 * @author: Emily
 * @create: 2021/08/21
 */
@RestController
@RequestMapping("/api/exception")
public class ExceptionController {

    @GetMapping("test1")
    public void exception(){
        String s = null;
        s.length();
    }
    @PostMapping("test2")
    public void customexception(@Validated @RequestBody Job job) throws ApiException {
        throw new ApiException("12", "自定义", "34");
    }
}
