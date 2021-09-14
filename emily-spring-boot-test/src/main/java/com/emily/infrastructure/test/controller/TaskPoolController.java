package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.context.helper.ThreadPoolHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description: 线程池测试
 * @author: Emily
 * @create: 2021/09/14
 */
@RestController
public class TaskPoolController {

    @GetMapping("test")
    public void test(){
        ThreadPoolHelper.threadPoolTaskExecutor().submit(() -> System.out.println("------runing..."));
    }
}
