package com.sgrain.boot.quartz.model;

import org.quartz.JobDataMap;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description: Job任务配置信息
 * @create: 2020/09/01
 */
public class SgJobDetail implements Serializable {
    //任务描述
    private String description;
    //任务名称
    private String jobName;
    //任务分组
    private String jobGroup;
    //是否需要持久化
    private boolean duration = true;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isDuration() {
        return duration;
    }

    public void setDuration(boolean duration) {
        this.duration = duration;
    }
}
