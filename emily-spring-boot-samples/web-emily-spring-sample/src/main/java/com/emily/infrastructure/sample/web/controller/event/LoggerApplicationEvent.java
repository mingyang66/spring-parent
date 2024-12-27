package com.emily.infrastructure.sample.web.controller.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author :  姚明洋
 * @since :  2024/12/27 上午10:44
 */
public class LoggerApplicationEvent extends ApplicationEvent {
    private final String source;

    public LoggerApplicationEvent(String source) {
        super(source);
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }
}
