package com.emily.boot.quartz.service;

import com.emily.boot.quartz.model.AddQuartzEntity;
import com.emily.boot.quartz.model.UpdateQuartzEntity;

public interface TaskService {
    /**
     * 新增触发器及调度任务
     *
     * @param addQuartzEntity
     * @return
     */
    String scheduleJob(AddQuartzEntity addQuartzEntity);

    /**
     * 更新触发器并关联老的调度任务
     *
     * @param updateQuartzEntity
     */
    void rescheduleJob(UpdateQuartzEntity updateQuartzEntity);

    /**
     * 暂停所有触发器
     */
    void pauseAll();

    /**
     * 暂停触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    void pauseTrigger(String triggerName, String triggerGroup);

    /**
     * 暂停触发器
     *
     * @param triggerGroup 触发器分组
     */
    void pauseTriggers(String triggerGroup);

    /**
     * 恢复触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    void resumeTrigger(String triggerName, String triggerGroup);

    /**
     * 恢复指定分组的task任务异常
     *
     * @param triggerGroup 触发器分组
     */
    void resumeTriggers(String triggerGroup);

    /**
     * 恢复所有task任务异常
     */
    void resumeAll();

    /**
     * 删除触发器及其关联的任务job
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    void unscheduleJob(String triggerName, String triggerGroup);
}
