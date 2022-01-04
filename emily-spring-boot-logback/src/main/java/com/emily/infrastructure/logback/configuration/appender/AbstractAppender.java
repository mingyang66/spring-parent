package com.emily.infrastructure.logback.configuration.appender;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.emily.infrastructure.logback.LogbackProperties;

/**
 * @program: spring-parent
 * @description: Appender抽象类
 * @author: 姚明洋
 * @create: 2022/01/04
 */
public abstract class AbstractAppender {
    /**
     * logger上下文
     */
    private LoggerContext loggerContext;
    /**
     * 属性配置
     */
    private LogbackProperties properties;

    public AbstractAppender(LoggerContext loggerContext, LogbackProperties properties) {
        this.loggerContext = loggerContext;
        this.properties = properties;
    }

    /**
     * 获取appender对象
     *
     * @param level appender过滤日志级别
     * @return
     */
    public abstract Appender<ILoggingEvent> getAppender(Level level);

    /**
     * 获取文件路径
     *
     * @param level 日志级别
     * @return
     */
    public abstract String getFilePath(Level level);

    /**
     * 获取日志输出格式
     *
     * @return
     */
    public abstract String getFilePattern();

    public LoggerContext getLoggerContext() {
        return loggerContext;
    }

    public void setLoggerContext(LoggerContext loggerContext) {
        this.loggerContext = loggerContext;
    }

    public LogbackProperties getProperties() {
        return properties;
    }

    public void setProperties(LogbackProperties properties) {
        this.properties = properties;
    }
}
