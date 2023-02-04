package com.emily.infrastructure.test.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * @Description :  事件监听类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/11/22 10:26 上午
 */
public class LoggerEventHandler implements EventHandler<LoggerEvent> {
    @Override
    public void onEvent(LoggerEvent testEvent, long l, boolean b) throws Exception {
        System.out.println("消费者："+testEvent.getValue());
    }
}
