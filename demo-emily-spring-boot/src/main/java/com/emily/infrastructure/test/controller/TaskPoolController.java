package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.core.context.ioc.IocUtils;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Emily
 * @program: spring-parent
 * 线程池测试
 * @since 2021/09/14
 */
@RestController
public class TaskPoolController {

    @GetMapping("test")
    public void test() {
        ThreadPoolHelper.defaultThreadPoolTaskExecutor().submit(() -> System.out.println("------runing..."));
    }

    @GetMapping("test1")
    public void test1() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = IocUtils.getBean(ThreadPoolTaskScheduler.class);
        System.out.println(threadPoolTaskScheduler);
    }
}
