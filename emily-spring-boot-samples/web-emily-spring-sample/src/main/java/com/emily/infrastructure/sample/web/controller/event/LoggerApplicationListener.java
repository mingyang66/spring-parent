package com.emily.infrastructure.sample.web.controller.event;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author :  姚明洋
 * @since :  2024/12/27 上午11:06
 */
@Component
public class LoggerApplicationListener implements ApplicationListener<LoggerApplicationEvent> {
    @Async
    @Override
    public void onApplicationEvent(LoggerApplicationEvent event) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Listener:" + event.getSource());
    }

    @Override
    public boolean supportsAsyncExecution() {
        //return ApplicationListener.super.supportsAsyncExecution();
        return true;
    }
}
