package com.sgrain.boot.quartz.service.impl;

import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.quartz.job.ThreadPoolJob;
import com.sgrain.boot.quartz.model.SgJobDetail;
import com.sgrain.boot.quartz.model.SgTrigger;
import com.sgrain.boot.quartz.service.JobService;
import com.sgrain.boot.quartz.factory.QuartzFactoryBean;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: spring-parent
 * @description: 定时任务业务实现逻辑
 * @author: 姚明洋
 * @create: 2020/09/01
 */
@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private Scheduler scheduler;

    @Override
    public boolean checkExists() {
        try {
            return scheduler.checkExists(JobKey.jobKey("JobName", "JobGroup"));
        } catch (SchedulerException e){

        }
        return false;
    }

    @Override
    public void addJob(SgJobDetail sgJobDetail) {
        try {
            JobDetail jobDetail = QuartzFactoryBean.newJob(ThreadPoolJob.class,null, sgJobDetail);
            scheduler.addJob(jobDetail, true);
        } catch (SchedulerException e) {
            throw new BusinessException(AppHttpStatus.API_EXCEPTION.getStatus(), "新增Job任务异常"+e.getMessage());
        }
    }

    @Override
    public void addTrigger(SgTrigger sgTrigger) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(sgTrigger.getTriggerName(), sgTrigger.getTriggerGroup()));
            Trigger trigger = QuartzFactoryBean.newTrigger(jobDetail, null, sgTrigger);
            scheduler.scheduleJob(trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
