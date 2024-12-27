package com.emily.infrastructure.logger.event;

import com.emily.infrastructure.logback.entity.BaseLogger;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

/**
 * 打印日志事件
 *
 * @author :  Emily
 * @since :  2024/12/278下午11:11
 */
public class LoggerPrintApplicationEvent extends ApplicationEvent {
    /**
     * 事件类型
     */
    private final EventType eventType;
    /**
     * 日志对象
     */
    private final BaseLogger baseLogger;

    public LoggerPrintApplicationEvent(EventType eventType, BaseLogger baseLogger) {
        super(baseLogger);
        Assert.notNull(eventType, "eventType must not be null");
        Assert.notNull(baseLogger, "baseLogger must not be null");
        this.eventType = eventType;
        this.baseLogger = baseLogger;
    }

    public BaseLogger getBaseLogger() {
        return baseLogger;
    }

    public EventType getEventType() {
        return eventType;
    }
}
