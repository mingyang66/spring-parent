package com.sgrain.boot.quartz.api;

import com.sgrain.boot.quartz.model.AddQuartzEntity;
import com.sgrain.boot.quartz.model.UpdateQuartzEntity;
import com.sgrain.boot.quartz.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: spring-parent
 * @description: 任务控制器类
 * @author: 姚明洋
 * @create: 2020/09/01
 */
@RestController
@RequestMapping("quartz")
public class TaskController {
    @Autowired
    private TaskService taskService;
    /**
     * 新增触发器及调度任务
     *
     * @param addQuartzEntity
     * @return
     */
    @PostMapping("scheduleJob")
    public String scheduleJob(@Validated @RequestBody AddQuartzEntity addQuartzEntity) {
        return taskService.scheduleJob(addQuartzEntity);
    }

    /**
     * 更新触发器并关联老的调度任务
     *
     * @param updateQuartzEntity
     */
    @PostMapping("rescheduleJob")
    public void rescheduleJob(@Validated @RequestBody UpdateQuartzEntity updateQuartzEntity) {
        taskService.rescheduleJob(updateQuartzEntity);
    }

    /**
     * 暂停触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    @GetMapping("pauseTrigger")
    public void pauseTrigger(String triggerName, String triggerGroup) {
        taskService.pauseTrigger(triggerName, triggerGroup);
    }

    /**
     * 暂停触发器
     *
     * @param triggerGroup 触发器分组
     */
    @GetMapping("pauseTriggers")
    public void pauseTriggers(String triggerGroup) {
        taskService.pauseTriggers(triggerGroup);
    }

    /**
     * 暂停所有触发器
     */
    @GetMapping("pauseAll")
    public void pauseAll() {
        taskService.pauseAll();
    }

    /**
     * 恢复触发器
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    @GetMapping("resumeTrigger")
    public void resumeTrigger(String triggerName, String triggerGroup) {
        taskService.resumeTrigger(triggerName, triggerGroup);
    }

    /**
     * 恢复指定分组的task任务异常
     *
     * @param triggerGroup 触发器分组
     */
    @GetMapping("resumeTriggers")
    public void resumeTriggers(String triggerGroup) {
        taskService.resumeTriggers(triggerGroup);
    }

    /**
     * 恢复所有task任务异常
     */
    @GetMapping("resumeAll")
    public void resumeAll() {
        taskService.resumeAll();
    }

    /**
     * 删除触发器及其关联的任务job
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     */
    @GetMapping("unscheduleJob")
    public void unscheduleJob(String triggerName, String triggerGroup) {
        taskService.unscheduleJob(triggerName, triggerGroup);
    }
}
