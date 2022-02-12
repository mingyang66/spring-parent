package com.emily.infrastructure.test.schedule;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * @program: spring-parent
 * @description: 自定义任务
 * @author: Emily
 * @create: 2021/09/15
 */
public class MyTask {

    @Scheduled(fixedDelay = 1000)
    public void work() {
        System.out.println("--定时任务--");
    }
}
