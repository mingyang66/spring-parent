package com.emily.infrastructure.logback.configuration.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logback.configuration.entity.LogbackAppender;
import com.emily.infrastructure.logback.configuration.enumeration.LogbackType;

/**
 * @program: spring-parent
 * @description: 日志组件抽象类
 * @author: Emily
 * @create: 2021/07/08
 */
public class LogbackRootImpl extends AbstractLogback {

    public LogbackRootImpl(LogbackProperties properties) {
        super(properties);
    }

    /**
     * 构建RootLogger对象，需在配置类中主动调用进行初始化
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     */
    @Override
    public Logger getLogger() {
        // 配置日志级别
        Level level = Level.toLevel(this.getProperties().getRoot().getLevel().levelStr);
        // 获取logger对象
        Logger rootLogger = this.getLoggerContext().getLogger(Logger.ROOT_LOGGER_NAME);
        //设置是否向上级打印信息
        rootLogger.setAdditive(false);
        // 设置日志级别
        rootLogger.setLevel(level);

        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(this.getLoggerContext(), this.getProperties());
        // 获取帮助类对象
        LogbackAppender logbackAppender = new LogbackAppender(Logger.ROOT_LOGGER_NAME, null, null, LogbackType.ROOT);
        // 是否开启异步日志
        if (this.getProperties().getAsync().isEnabled()) {
            //异步appender
            LogbackAsyncAppender asyncAppender = new LogbackAsyncAppender(this.getLoggerContext(), this.getProperties());
            if (level.levelInt <= Level.ERROR_INT) {
                rootLogger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.ERROR))));
            }
            if (level.levelInt <= Level.WARN_INT) {
                rootLogger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.WARN))));
            }
            if (level.levelInt <= Level.INFO_INT) {
                rootLogger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.INFO))));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                rootLogger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.DEBUG))));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                rootLogger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.TRACE))));
            }
        } else {
            if (level.levelInt <= Level.ERROR_INT) {
                rootLogger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.ERROR)));
            }
            if (level.levelInt <= Level.WARN_INT) {
                rootLogger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.WARN)));
            }
            if (level.levelInt <= Level.INFO_INT) {
                rootLogger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.INFO)));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                rootLogger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.DEBUG)));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                rootLogger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.TRACE)));
            }
        }
        return rootLogger;
    }
}
