package com.yaomy.handler.api;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@RestController
public class HandlerController {
    @RequestMapping(value = "/handler/test")
    public User getName(@RequestBody @Valid User user){
        return user;
    }
}
