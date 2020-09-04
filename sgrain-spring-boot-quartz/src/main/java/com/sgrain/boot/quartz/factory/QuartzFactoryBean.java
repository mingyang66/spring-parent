package com.sgrain.boot.quartz.factory;

import com.sgrain.boot.quartz.model.SgJobDetail;
import com.sgrain.boot.quartz.model.SgTrigger;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Objects;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/08/28
 */
public class QuartzFactoryBean {
    /**
     * 创建Job任务信息类
     * @param jobBean
     * @param jobDataMap
     * @param jobDetail
     * @return
     */
    public static JobDetail newJob(Class<? extends QuartzJobBean> jobBean, JobDataMap jobDataMap, SgJobDetail jobDetail){
        JobBuilder builder = JobBuilder.newJob(jobBean);
        //是否持久化
        builder.storeDurably(jobDetail.isDuration());
        //描述
        builder.withDescription(jobDetail.getDescription());
        //任务名称和任务分组 组合成任务唯一标识
        builder.withIdentity(JobKey.jobKey(jobDetail.getJobName(), jobDetail.getJobGroup()));
        //Job任务参数集合
        if(Objects.nonNull(jobDataMap)){
            builder.usingJobData(jobDataMap);
        }
        return builder.build();
    }

    /**
     * 创建Trigger
     * @param jobDetail
     * @param sgTrigger
     * @return
     */
    public static Trigger newTrigger(JobDetail jobDetail, JobDataMap jobDataMap, SgTrigger sgTrigger){
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger(); //创建一个定义或者构建触发器的builder实例
        //通过从给定的作业中提取出jobKey,设置由生成的触发器触发的作业的标识
        builder.forJob(jobDetail);
        //自定义触发器描述
        builder.withDescription(sgTrigger.getDescription());
        //设置触发器名称、触发器分组，组合为触发器唯一标识
        builder.withIdentity(TriggerKey.triggerKey(sgTrigger.getTriggerName()+"Trigger", sgTrigger.getTriggerGroup()+"Trigger"));
        //如果有一个Job任务供多个触发器调度，而每个触发器调度传递不同的参数，此时JobDataMap可以提供不同的数据输入
        //在任务执行时JobExecutionContext提供不同的JobDataMap参数给Job
        if(Objects.nonNull(jobDataMap)){
            builder.usingJobData(jobDataMap);
        }
        //触发器优先级
        builder.withPriority(sgTrigger.getPriority());
        //设置用于定义触发器的{@link org.quartz.ScheduleBuilder}配置计划 "0/10 * * * * ? "
        builder.withSchedule(CronScheduleBuilder.cronSchedule(sgTrigger.getCron()));
        //将触发器的启动时间设置为当前时刻，触发器可能此时触发，也可能不触发，这取决于为触发器配置的计划
        builder.startNow();
        //构造触发器
        return builder.build();

    }
}
