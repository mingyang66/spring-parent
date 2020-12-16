package com.emily.boot.test.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/12/16
 */
@RestController
public class TestController {

    @GetMapping("test")
    public String test(){
        return "success";
    }
}
