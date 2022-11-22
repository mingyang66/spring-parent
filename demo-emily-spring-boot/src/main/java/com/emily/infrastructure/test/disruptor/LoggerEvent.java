package com.emily.infrastructure.test.disruptor;

/**
 * @Description :  事件
 * @Author :  姚明洋
 * @CreateDate :  Created in 2022/11/22 10:22 上午
 */
public class LoggerEvent {
    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
