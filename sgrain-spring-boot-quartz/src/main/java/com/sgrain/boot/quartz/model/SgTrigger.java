package com.sgrain.boot.quartz.model;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description: 触发器信息类
 * @create: 2020/09/01
 */
public class SgTrigger implements Serializable {
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
}
