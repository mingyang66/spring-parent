package com.sgrain.boot.quartz.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: Job任务配置信息
 * @create: 2020/09/01
 */
public class AddQuartzEntity implements Serializable {
    //任务名称
    private String taskName;
    //任务分组
    private String taskGroup;
    //任务描述
    private String description;
    //cron表达式
    private String cron;
    //作业任务参数
    private Map<String, Object> taskParams;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Map<String, Object> getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(Map<String, Object> taskParams) {
        this.taskParams = taskParams;
    }
}
