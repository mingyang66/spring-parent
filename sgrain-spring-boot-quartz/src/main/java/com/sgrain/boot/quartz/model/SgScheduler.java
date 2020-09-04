package com.sgrain.boot.quartz.model;

import org.quartz.JobDataMap;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/09/01
 */
public class SgScheduler implements Serializable {
    //描述
    private String description;
    //触发器名称
    private String triggerName;
    //触发器分组
    private String triggerGroup;
    //优先级
    private int priority = 5;
    //cron表达式
    private String cron;
    //Job任务属性
    private JobDataMap jobDataMap;

    //任务名称
    private String jobName;
    //任务分组
    private String jobGroup;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public JobDataMap getJobDataMap() {
        return jobDataMap;
    }

    public void setJobDataMap(JobDataMap jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
}
