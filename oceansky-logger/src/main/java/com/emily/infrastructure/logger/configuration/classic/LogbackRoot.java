package com.emily.infrastructure.logger.configuration.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.util.StatusPrinter;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.appender.AbstractAppender;
import com.emily.infrastructure.logger.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logger.configuration.appender.LogbackConsoleAppender;
import com.emily.infrastructure.logger.configuration.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logger.configuration.appender.LogbackAppender;
import com.emily.infrastructure.logger.configuration.type.LogbackType;

/**
 * @program: spring-parent
 * @description: 日志组件抽象类
 * @author: Emily
 * @create: 2021/07/08
 */
public class LogbackRoot extends AbstractLogback {
    private final LoggerProperties properties;
    public LogbackRoot(LoggerProperties properties) {
        this.properties=properties;
    }

    /**
     * 构建RootLogger对象，需在配置类中主动调用进行初始化
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     */
    @Override
    public Logger getLogger() {
        // 获取logger对象
        Logger logger = this.getLoggerContext().getLogger(Logger.ROOT_LOGGER_NAME);
        //设置是否向上级打印信息
        logger.setAdditive(false);
        // 设置日志级别
        logger.setLevel(Level.toLevel(properties.getRoot().getLevel().levelStr));
        // 获取帮助类对象
        LogbackAppender appender = new LogbackAppender(Logger.ROOT_LOGGER_NAME, properties.getRoot().getFilePath(), LogbackType.ROOT);
        // appender对象
        AbstractAppender rollingFileAppender = new LogbackRollingFileAppender(this.getLoggerContext(), properties, appender);
        // 是否开启异步日志
        if (properties.getAppender().getAsync().isEnabled()) {
            //异步appender
            LogbackAsyncAppender asyncAppender = new LogbackAsyncAppender(this.getLoggerContext(), properties);
            if (logger.getLevel().levelInt <= Level.ERROR_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.ERROR)));
            }
            if (logger.getLevel().levelInt <= Level.WARN_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.WARN)));
            }
            if (logger.getLevel().levelInt <= Level.INFO_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.INFO)));
            }
            if (logger.getLevel().levelInt <= Level.DEBUG_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.DEBUG)));
            }
            if (logger.getLevel().levelInt <= Level.TRACE_INT) {
                logger.addAppender(asyncAppender.getAppender(rollingFileAppender.getInstance(Level.TRACE)));
            }
        } else {
            if (logger.getLevel().levelInt <= Level.ERROR_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.ERROR));
            }
            if (logger.getLevel().levelInt <= Level.WARN_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.WARN));
            }
            if (logger.getLevel().levelInt <= Level.INFO_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.INFO));
            }
            if (logger.getLevel().levelInt <= Level.DEBUG_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.DEBUG));
            }
            if (logger.getLevel().levelInt <= Level.TRACE_INT) {
                logger.addAppender(rollingFileAppender.getInstance(Level.TRACE));
            }
        }
        if (!properties.getRoot().isConsole()) {
            //移除console控制台appender
            logger.detachAppender(LogbackConsoleAppender.CONSOLE_NAME);
        }
        //是否报告logback内部状态信息
        if (properties.getAppender().isReportState()) {
            StatusPrinter.print(this.getLoggerContext());
        }
        return logger;
    }
}
