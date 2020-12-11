package com.emily.boot.quartz.service.impl;

import com.emily.boot.quartz.job.CronJob;
import com.emily.boot.quartz.model.AddQuartzEntity;
import com.emily.boot.quartz.model.UpdateQuartzEntity;
import com.emily.boot.common.enums.AppHttpStatus;
import com.emily.boot.common.enums.DateFormatEnum;
import com.emily.boot.common.exception.BusinessException;
import com.emily.boot.common.utils.date.DateUtils;
import com.emily.boot.quartz.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @program: spring-parent
 * @description: 定时任务业务实现逻辑
 * @create: 2020/09/01
 */
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private Scheduler scheduler;

    /**
     * 新增触发器及调度任务
     *
     * @param addQuartzEntity
     * @return
     */
    @Override
    public String scheduleJob(AddQuartzEntity addQuartzEntity) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(CronJob.class)
                    .withDescription(addQuartzEntity.getDescription())
                    //任务名称和任务分组 组合成任务唯一标识
                    .withIdentity(JobKey.jobKey(addQuartzEntity.getTaskName(), addQuartzEntity.getTaskGroup()))
                    //如果一个job是非持久的，当没有活跃的trigger与之关联的时候，会被自动地从scheduler中删除。也就是说，非持久的job的生命期是由trigger的存在与否决定的；
                    .storeDurably(false)
                    //如果一个job是可恢复的，并且在其执行的时候，scheduler发生硬关闭（hard shutdown)（比如运行的进程崩溃了，或者关机了），则当scheduler重新启动的时候，该job会被重新执行。此时，该job的JobExecutionContext.isRecovering() 返回true。
                    .requestRecovery(false)
                    .build();

            TriggerBuilder builder = TriggerBuilder.newTrigger()
                    //通过从给定的作业中提取出jobKey,设置由生成的触发器触发的作业的标识
                    .forJob(jobDetail)
                    //作业优先级
                    .withPriority(5)
                    //描述
                    .withDescription(addQuartzEntity.getDescription())
                    //设置触发器名称、触发器分组，组合为触发器唯一标识
                    .withIdentity(TriggerKey.triggerKey(addQuartzEntity.getTaskName(), addQuartzEntity.getTaskGroup()))
                    //触发器参数，传递给调度任务
                    .usingJobData("url", addQuartzEntity.getTaskParam())
                    //设置用于定义触发器的{@link org.quartz.ScheduleBuilder}配置计划 "0/10 * * * * ? "
                    .withSchedule(CronScheduleBuilder.cronSchedule(addQuartzEntity.getCron()));

            if (StringUtils.isNotEmpty(addQuartzEntity.getStartDate())) {
                //触发器开始执行时间，默认当前时间
                builder.startAt(DateUtils.parseDate(addQuartzEntity.getStartDate(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
            }
            if (StringUtils.isNotEmpty(addQuartzEntity.getEndDate())) {
                //设置触发器执行结束时间，如果null,则触发器结束时间不确定
                builder.endAt(DateUtils.parseDate(addQuartzEntity.getEndDate(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
            }
            //返回第一次任务执行时间
            Date date = scheduler.scheduleJob(jobDetail, builder.build());
            return DateFormatUtils.format(date, DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat());
        } catch (SchedulerException e) {
            throw new BusinessException(AppHttpStatus.API_EXCEPTION.getStatus(), "新增Task任务异常" + e.getMessage());
        }
    }

    /**
     * 更新触发器并关联老的调度任务
     *
     * @param updateQuartzEntity
     */
    @Override
    public void rescheduleJob(UpdateQuartzEntity updateQuartzEntity) {
        try {
            TriggerBuilder builder = TriggerBuilder.newTrigger()
                    //作业优先级
                    .withPriority(5)
                    //描述
                    .withDescription(updateQuartzEntity.getDescription())
                    //设置触发器名称、触发器分组，组合为触发器唯一标识
                    .withIdentity(TriggerKey.triggerKey(updateQuartzEntity.getTaskName(), updateQuartzEntity.getTaskGroup()))
                    //设置用于定义触发器的{@link org.quartz.ScheduleBuilder}配置计划 "0/10 * * * * ? "
                    .withSchedule(CronScheduleBuilder.cronSchedule(updateQuartzEntity.getCron()))
                    //触发器参数，传递给调度任务
                    .usingJobData("url", updateQuartzEntity.getTaskParam())
                    //指定被更新触发器的task任务
                    .forJob(JobKey.jobKey(updateQuartzEntity.getOldTaskName(), updateQuartzEntity.getOldTaskGroup()));
            if (StringUtils.isNotEmpty(updateQuartzEntity.getStartDate())) {
                //触发器开始执行时间，默认当前时间
                builder.startAt(DateUtils.parseDate(updateQuartzEntity.getStartDate(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
            }
            if (StringUtils.isNotEmpty(updateQuartzEntity.getEndDate())) {
                //设置触发器执行结束时间，如果null,则触发器结束时间不确定
                builder.endAt(DateUtils.parseDate(updateQuartzEntity.getEndDate(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
            }
            TriggerKey oldTriggerKey = TriggerKey.triggerKey(updateQuartzEntity.getOldTaskName(), updateQuartzEntity.getOldTaskGroup());
            scheduler.rescheduleJob(oldTriggerKey, builder.build());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    @Override
    public void pauseTrigger(String triggerName, String triggerGroup) {
        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
        } catch (SchedulerException e) {
            throw new BusinessException(AppHttpStatus.API_EXCEPTION.getStatus(), "暂停Task任务异常" + e.getMessage());
        }
    }

    /**
     * 暂停触发器
     *
     * @param triggerGroup 触发器分组
     */
    @Override
    public void pauseTriggers(String triggerGroup) {
        try {
            scheduler.pauseTriggers(GroupMatcher.triggerGroupEquals(triggerGroup));
        } catch (SchedulerException e) {
            throw new BusinessException(AppHttpStatus.API_EXCEPTION.getStatus(), "暂停Task任务异常" + e.getMessage());
        }
    }

    /**
     * 暂停所有触发器
     */
    @Override
    public void pauseAll() {
        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            throw new BusinessException(AppHttpStatus.API_EXCEPTION.getStatus(), "暂停Task任务异常" + e.getMessage());
        }
    }

    /**
     * 恢复触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    @Override
    public void resumeTrigger(String triggerName, String triggerGroup) {
        try {
            scheduler.resumeTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
        } catch (SchedulerException e) {
            throw new BusinessException(AppHttpStatus.API_EXCEPTION.getStatus(), "恢复Task任务异常" + e.getMessage());
        }
    }

    /**
     * 恢复指定分组的task任务异常
     *
     * @param triggerGroup 触发器分组
     */
    @Override
    public void resumeTriggers(String triggerGroup) {
        try {
            scheduler.resumeTriggers(GroupMatcher.triggerGroupEquals(triggerGroup));
        } catch (SchedulerException e) {
            throw new BusinessException(AppHttpStatus.API_EXCEPTION.getStatus(), "恢复指定分组的Task任务异常" + e.getMessage());
        }
    }

    /**
     * 恢复所有task任务异常
     */
    @Override
    public void resumeAll() {
        try {
            scheduler.resumeAll();
        } catch (SchedulerException e) {
            throw new BusinessException(AppHttpStatus.API_EXCEPTION.getStatus(), "恢复所有Task任务异常" + e.getMessage());
        }
    }

    /**
     * 删除触发器及其关联的任务job
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    @Override
    public void unscheduleJob(String triggerName, String triggerGroup) {
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroup));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


}
