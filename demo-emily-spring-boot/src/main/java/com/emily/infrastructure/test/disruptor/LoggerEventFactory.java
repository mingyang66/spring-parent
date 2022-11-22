package com.emily.infrastructure.test.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @Description :  事件工厂
 * @Author :  姚明洋
 * @CreateDate :  Created in 2022/11/22 10:23 上午
 */
public class LoggerEventFactory implements EventFactory<LoggerEvent> {
    @Override
    public LoggerEvent newInstance() {
        return new LoggerEvent();
    }
}
