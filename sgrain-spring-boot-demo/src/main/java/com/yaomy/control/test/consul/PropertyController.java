package com.yaomy.control.test.consul;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/11/10
 */
@RestController
public class PropertyController {
    @Autowired
    private Environment environment;

    @GetMapping("consul/test")
    public String test(){
        return environment.getProperty("test");
    }
}
