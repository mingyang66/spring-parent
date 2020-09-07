package com.sgrain.boot.quartz.job;

import com.sgrain.boot.common.enums.DateFormatEnum;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

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
        System.out.println(DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()) +"接收到的地址是："+url);
    }
}
