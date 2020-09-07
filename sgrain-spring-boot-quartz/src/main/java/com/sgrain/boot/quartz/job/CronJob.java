package com.sgrain.boot.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/08/28
 */
public class CronJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Trigger trigger = context.getTrigger();
        String url = context.getTrigger().getJobDataMap().getString("url");
        System.out.println("接收到的地址是："+url);
    }
}
