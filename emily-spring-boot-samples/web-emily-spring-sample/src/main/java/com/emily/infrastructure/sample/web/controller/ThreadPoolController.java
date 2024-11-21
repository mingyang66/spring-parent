package com.emily.infrastructure.sample.web.controller;

import com.emily.infrastructure.tracing.ioc.IocUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Emily
 * @program: spring-parent
 * 线程池监控控制器
 * @since 2020/08/25
 */
@RestController
@RequestMapping("threadPool")
public class ThreadPoolController {

    @Autowired
    @Lazy
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @GetMapping("metrics")
    public Map<String, Object> metrics() {
        ThreadPoolExecutor executor = asyncTaskExecutor.getThreadPoolExecutor();
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("ThreadNamePrefix（线程名前缀）", asyncTaskExecutor.getThreadNamePrefix());
        map.put("CorePoolSize（核心线程数）", executor.getCorePoolSize());
        map.put("MaxPoolSize（最大线程池大小）", asyncTaskExecutor.getMaxPoolSize());
        map.put("ActiveCount（正在执行任务的线程数）", executor.getActiveCount());
        map.put("TaskCount（计划执行的任务总数）", executor.getTaskCount());
        map.put("CompletedTaskCount（已完成任务总数）", executor.getCompletedTaskCount());
        map.put("MaximumPoolSize（允许的最大线程数）", executor.getMaximumPoolSize());
        map.put("LargestPoolSize（池中同时存在的最大线程数）", executor.getLargestPoolSize());
        map.put("PoolSize（当前池中的线程数）", executor.getPoolSize());
        map.put("KeepAliveTime（空闲时间）", executor.getKeepAliveTime(TimeUnit.SECONDS));
        map.put("Queue（队列中线程数）", executor.getQueue().size());
        map.put("RemainingCapacity（队列大小）", executor.getQueue().remainingCapacity());
        return map;
    }

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
