package com.sgrain.boot.web.api.controller;

import com.google.common.collect.Maps;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 线程池监控控制器
 * @create: 2020/08/25
 */
//@RestController
//@RequestMapping("threadPool")
@Component
public class ThreadPoolController {

    @Autowired
    @Lazy
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    @Scheduled(cron = "0/10 * * * * ?")
    //@GetMapping("metrics")
    public Map<String, Object> metrics(){
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
        System.out.println(JSONUtils.toJSONPrettyString(map));
        return map;
    }
}
