package com.sgrain.boot.quartz.config;

import com.sgrain.boot.quartz.listener.MonitorTriggerListener;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerListener;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: quartz相关配置
 * @author: 姚明洋
 * @create: 2020/09/07
 */
@SpringBootConfiguration
//@Configuration
public class QuartzConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Bean
    @ConditionalOnClass(Scheduler.class)
    public TriggerListener jobListenerSupport(Scheduler scheduler) {
        Scheduler scheduler1 = applicationContext.getBean(Scheduler.class);
        try {
            ListenerManager listenerManager = scheduler.getListenerManager();
            listenerManager.addTriggerListener(new MonitorTriggerListener());
            System.out.println(listenerManager.getJobListeners().size());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
