package com.yaomy.control.test.api.rabbit;

import com.yaomy.control.test.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/08/21
 */
@RestController
@RequestMapping("async")
public class AsyncController {
    @Autowired
    private AsyncService asyncService;

    @GetMapping("test1")
    public String test1(@RequestParam(required = false) String name){
        return asyncService.async1(name);
    }
}
