package com.emily.framework.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.stereotype.Component;

/**
 * @program: spring-parent
 * @description: 触发器监听器
 * @create: 2020/09/05
 */
@Component
public class MonitorTriggerListener implements TriggerListener {
    @Override
    public String getName() {
        return "MonitorTriggerListener";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        System.out.println("----------triggerFired---------------");
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        System.out.println("------------vetoJobExecution-------------");
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        System.out.println("-------------triggerMisfired------------");
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        System.out.println("--------------triggerComplete-----------");
    }
}
