package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.test.po.Job;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: emily-infrustructure
 * @description: 参数控制器
 * @author: Emily
 * @create: 2021/10/30
 */
@RestController
@RequestMapping("param")
public class ParamController {
    @GetMapping("test")
    public String test() {
        return "afdss";
    }

    @PostMapping("test1")
    public String test1(@Validated @RequestBody Job job) {
        return "afdss-"+job.getJobDesc();
    }
}
