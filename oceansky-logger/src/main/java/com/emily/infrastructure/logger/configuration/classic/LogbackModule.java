package com.emily.infrastructure.logger.configuration.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.configuration.appender.AbstractAppender;
import com.emily.infrastructure.logger.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logger.configuration.appender.LogbackConsoleAppender;
import com.emily.infrastructure.logger.configuration.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logger.configuration.property.LogbackAppender;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;

/**
 * @program: spring-parent
 * @description: 分组记录日志
 * @author: Emily
 * @create: 2021/12/12
 */
public class LogbackModule extends AbstractLogback {
    private final LoggerProperties properties;
    private final LoggerContext context;

    public LogbackModule(LoggerProperties properties, LoggerContext context) {
        this.properties = properties;
        this.context = context;
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param loggerName logger属性名
     * @param appender   appender
     * @return 日志对象
     */
    @Override
    public Logger getLogger(String loggerName, LogbackAppender appender) {
        // 获取Logger对象
        Logger logger = context.getLogger(loggerName);
        // 设置是否向上级打印信息
        logger.setAdditive(false);
        // 设置日志级别
        logger.setLevel(Level.toLevel(properties.getModule().getLevel().levelStr));
        // appender对象
        AbstractAppender rollingFileAppender = new LogbackRollingFileAppender(properties, context, appender);
        // 是否开启异步日志
        if (properties.getAppender().getAsync().isEnabled()) {
            //异步appender
            LogbackAsyncAppender asyncAppender = new LogbackAsyncAppender(properties, context);
            if (logger.getLevel().levelInt == Level.ERROR_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.newInstance(Level.ERROR)));
            }
            if (logger.getLevel().levelInt == Level.WARN_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.newInstance(Level.WARN)));
            }
            if (logger.getLevel().levelInt == Level.INFO_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.newInstance(Level.INFO)));
            }
            if (logger.getLevel().levelInt == Level.DEBUG_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.newInstance(Level.DEBUG)));
            }
            if (logger.getLevel().levelInt == Level.TRACE_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.newInstance(Level.TRACE)));
            }
        } else {
            if (logger.getLevel().levelInt == Level.ERROR_INT) {
                logger.addAppender(rollingFileAppender.newInstance(Level.ERROR));
            }
            if (logger.getLevel().levelInt == Level.WARN_INT) {
                logger.addAppender(rollingFileAppender.newInstance(Level.WARN));
            }
            if (logger.getLevel().levelInt == Level.INFO_INT) {
                logger.addAppender(rollingFileAppender.newInstance(Level.INFO));
            }
            if (logger.getLevel().levelInt == Level.DEBUG_INT) {
                logger.addAppender(rollingFileAppender.newInstance(Level.DEBUG));
            }
            if (logger.getLevel().levelInt == Level.TRACE_INT) {
                logger.addAppender(rollingFileAppender.newInstance(Level.TRACE));
            }
        }
        if (properties.getModule().isConsole()) {
            // 添加控制台appender
            logger.addAppender(new LogbackConsoleAppender(properties, context).newInstance(logger.getLevel()));
        }

        return logger;
    }
}
