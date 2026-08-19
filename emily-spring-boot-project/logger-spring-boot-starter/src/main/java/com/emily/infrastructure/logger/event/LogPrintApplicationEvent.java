package com.emily.infrastructure.logger.event;

import com.emily.infrastructure.logback.entity.BaseLogger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

/**
 * 打印日志事件
 *
 * @author :  Emily
 * @since :  2024/12/278下午11:11
 */
public class LogPrintApplicationEvent extends ApplicationEvent {
    /**
     * 当前事件上下文
     */
    private ApplicationContext context;
    /**
     * 事件类型
     */
    private final LogEventType eventType;
    /**
     * 日志对象
     */
    private final BaseLogger baseLogger;

    public LogPrintApplicationEvent(ApplicationContext context, LogEventType eventType, BaseLogger baseLogger) {
        super(baseLogger);
        Assert.notNull(eventType, "eventType must not be null");
        Assert.notNull(baseLogger, "baseLogger must not be null");
        this.eventType = eventType;
        this.baseLogger = baseLogger;
        this.context = context;
    }

    public BaseLogger getBaseLogger() {
        return baseLogger;
    }

    public LogEventType getEventType() {
        return eventType;
    }

    public ApplicationContext getContext() {
        return context;
    }
}
