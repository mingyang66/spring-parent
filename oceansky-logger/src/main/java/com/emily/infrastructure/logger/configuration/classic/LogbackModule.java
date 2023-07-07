package com.emily.infrastructure.logger.configuration.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.emily.infrastructure.logger.configuration.appender.*;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.LogbackType;

/**
 * @program: spring-parent
 * @description: 分组记录日志
 * @author: Emily
 * @create: 2021/12/12
 */
public class LogbackModule extends AbstractLogback {
    private final LoggerProperties properties;

    public LogbackModule(LoggerProperties properties) {
        this.properties = properties;
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param loggerName   logger属性名
     * @param appenderName appender属性名
     * @param filePath     文件路径
     * @param fileName     文件名
     * @return
     */
    @Override
    public Logger getLogger(String loggerName, String appenderName, String filePath, String fileName) {
        // 获取Logger对象
        Logger logger = this.getLoggerContext().getLogger(loggerName);
        // 设置是否向上级打印信息
        logger.setAdditive(false);
        // 设置日志级别
        logger.setLevel(Level.toLevel(properties.getModule().getLevel().levelStr));
        // 获取帮助类对象
        LogbackAppender appender = new LogbackAppender(appenderName, filePath, fileName, LogbackType.MODULE);
        // appender对象
        AbstractAppender rollingFileAppender = new LogbackRollingFileAppender(this.getLoggerContext(), properties, appender);
        // 是否开启异步日志
        if (properties.getAppender().getAsync().isEnabled()) {
            //异步appender
            LogbackAsyncAppender asyncAppender = new LogbackAsyncAppender(this.getLoggerContext(), properties);
            if (logger.getLevel().levelInt == Level.ERROR_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.ERROR)));
            }
            if (logger.getLevel().levelInt == Level.WARN_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.WARN)));
            }
            if (logger.getLevel().levelInt == Level.INFO_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.INFO)));
            }
            if (logger.getLevel().levelInt == Level.DEBUG_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.DEBUG)));
            }
            if (logger.getLevel().levelInt == Level.TRACE_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.TRACE)));
            }
        } else {
            if (logger.getLevel().levelInt == Level.ERROR_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.ERROR));
            }
            if (logger.getLevel().levelInt == Level.WARN_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.WARN));
            }
            if (logger.getLevel().levelInt == Level.INFO_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.INFO));
            }
            if (logger.getLevel().levelInt == Level.DEBUG_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.DEBUG));
            }
            if (logger.getLevel().levelInt == Level.TRACE_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.TRACE));
            }
        }
        if (properties.getModule().isConsole()) {
            // 添加控制台appender
            logger.addAppender(new LogbackConsoleAppender(this.getLoggerContext(), properties).getInstance(logger.getLevel()));
        }

        return logger;
    }
}
