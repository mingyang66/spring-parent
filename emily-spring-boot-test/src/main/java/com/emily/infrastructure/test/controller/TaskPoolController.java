package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.context.helper.ThreadPoolHelper;
import com.emily.infrastructure.context.ioc.IOCContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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

    @GetMapping("test1")
    public void test1(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler = IOCContext.getBean(ThreadPoolTaskScheduler.class);
        System.out.println(threadPoolTaskScheduler);
    }
}
