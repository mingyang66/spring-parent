package com.emily.infrastructure.logback.configuration.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackConsoleAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logback.configuration.entity.LogbackAppender;
import com.emily.infrastructure.logback.configuration.enumeration.LogbackType;

/**
 * @program: spring-parent
 * @description: 分组记录日志
 * @author: Emily
 * @create: 2021/12/12
 */
public class LogbackGroupImpl extends AbstractLogback {

    public LogbackGroupImpl(LogbackProperties properties) {
        super(properties);
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    @Override
    public Logger getLogger(String appenderName, String logbackPath, String fileName) {
        // 配置日志级别
        Level level = Level.toLevel(this.getProperties().getGroup().getLevel().levelStr);
        // 获取logger对象
        Logger logger = this.getLoggerContext().getLogger(appenderName);
        // 设置是否向上级打印信息
        logger.setAdditive(false);
        // 设置日志级别
        logger.setLevel(level);

        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(this.getLoggerContext(), this.getProperties());
        // 获取帮助类对象
        LogbackAppender logbackAppender = new LogbackAppender(appenderName, logbackPath, fileName, LogbackType.GROUP);
        // 是否开启异步日志
        if (this.getProperties().getAsync().isEnabled()) {
            //异步appender
            LogbackAsyncAppender asyncAppender = new LogbackAsyncAppender(this.getLoggerContext(), this.getProperties());
            if (level.levelInt <= Level.ERROR_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.ERROR))));
            }
            if (level.levelInt <= Level.WARN_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.WARN))));
            }
            if (level.levelInt <= Level.INFO_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.INFO))));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.DEBUG))));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.TRACE))));
            }
        } else {
            if (level.levelInt <= Level.ERROR_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.ERROR)));
            }
            if (level.levelInt <= Level.WARN_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.WARN)));
            }
            if (level.levelInt <= Level.INFO_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.INFO)));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.DEBUG)));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.TRACE)));
            }
        }
        if (this.getProperties().getGroup().isConsole()) {
            // 添加控制台appender
            logger.addAppender(new LogbackConsoleAppender(this.getLoggerContext(), this.getProperties()).getConsoleAppender(level));
        }

        return logger;
    }
}
