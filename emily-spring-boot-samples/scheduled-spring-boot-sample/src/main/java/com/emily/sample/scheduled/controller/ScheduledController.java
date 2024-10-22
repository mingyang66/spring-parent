package com.emily.sample.scheduled.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author :  Emily
 * @since :  2024/10/22 下午4:57
 */
@RestController
public class ScheduledController {
    private final TaskScheduler taskScheduler;

    public ScheduledController(@Qualifier("sampleTaskScheduler") TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @GetMapping("api/scheduled/execute")
    public void execute() {
        taskScheduler.scheduleAtFixedRate(() -> System.out.println("当前时间是：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), Duration.ofSeconds(10));
    }
}
