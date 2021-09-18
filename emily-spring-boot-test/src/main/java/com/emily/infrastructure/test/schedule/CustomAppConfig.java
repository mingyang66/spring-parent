package com.emily.infrastructure.test.schedule;

import com.emily.infrastructure.context.helper.ThreadPoolHelper;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @program: spring-parent
 * @description: 基于接口的动态定时任务
 * @author: Emily
 * @create: 2021/09/15
 */
//@Configuration
//@EnableScheduling
public class CustomAppConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(executor());
        taskRegistrar.addTriggerTask(() -> {
                    System.out.println("-------do----------");
                    System.out.println("-------do----------");
                },
                triggerContext -> {
                    System.out.println("--");
                    CronTrigger cronTrigger = new CronTrigger("10/20 * * * * ? ");
                    return cronTrigger.nextExecutionTime(triggerContext);
                });
    }
    public Executor executor(){
        return Executors.newScheduledThreadPool(100);
    }
}
