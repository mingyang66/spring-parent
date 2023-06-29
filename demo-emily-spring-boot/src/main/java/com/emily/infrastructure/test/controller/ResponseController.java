package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.autoconfigure.response.annotation.ApiResponseWrapperIgnore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description :  返回值包装测试控制器
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/27 1:59 PM
 */
@RestController
@RequestMapping("api/response")
public class ResponseController {

    @GetMapping("wrapper")
    public String wrapper() {
        return "wrapper";
    }

    @GetMapping("wrapperException")
    public String wrapperException() {
        throw new IllegalArgumentException("wrapperException");
    }

    @GetMapping("ignore")
    @ApiResponseWrapperIgnore
    public String ignore() {
        return "ignore";
    }

    @GetMapping("ignoreException")
    @ApiResponseWrapperIgnore
    public String ignoreException() {
        throw new IllegalArgumentException("非法参数");
    }
}
