package com.emily.sample.scheduled.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author :  Emily
 * @since :  2024/10/22 下午4:48
 */
@Configuration
public class ScheduledConfiguration {

    @Bean("sampleTaskScheduler")
    protected TaskScheduler initDefaultTaskScheduler() {
        //定义心跳任务调度器
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        taskScheduler.setThreadNamePrefix("sampleTaskScheduled--");
        taskScheduler.setDaemon(true);
        // true-当任务被取消时，如果任务尚未开始执行，则会被从队列中移除。如果任务已经开始执行，则不会立即中断任务，但一旦任务完成，用于执行该任务的线程可能会被回收（取决于线程池的配置），
        // false- 即使任务被取消，它也不会从队列中移除，除非它正常完成或线程池关闭。
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.initialize();
        return taskScheduler;
    }
}
