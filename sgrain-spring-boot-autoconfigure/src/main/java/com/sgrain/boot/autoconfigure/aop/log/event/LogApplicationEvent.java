package com.sgrain.boot.autoconfigure.aop.log.event;

import org.springframework.context.ApplicationEvent;

/**
 * @program: spring-parent
 * @description: 日志事件
 * @create: 2020/08/07
 */
public class LogApplicationEvent extends ApplicationEvent {
    public LogApplicationEvent(Object source) {
        super(source);
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }
}
