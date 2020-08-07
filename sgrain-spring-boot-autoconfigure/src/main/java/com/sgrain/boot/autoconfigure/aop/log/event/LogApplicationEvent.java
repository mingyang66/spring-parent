package com.sgrain.boot.autoconfigure.aop.log.event;

import org.springframework.context.ApplicationEvent;

import java.util.EventObject;

/**
 * @program: spring-parent
 * @description: 日志事件
 * @author: 姚明洋
 * @create: 2020/08/07
 */
public class LogApplicationEvent extends ApplicationEvent {
    public LogApplicationEvent(LogAop source) {
        super(source);
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }
}
