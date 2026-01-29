package com.emily.infrastructure.logback.configuration.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.LevelUtil;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.common.LogPathField;
import com.emily.infrastructure.logback.configuration.appender.AbstractAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackConsoleAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logback.configuration.type.LogbackType;
import com.emily.infrastructure.logback.factory.LogBeanFactory;

/**
 * 分组记录日志
 *
 * @author Emily
 * @since : 2021/12/12
 */
public class LogbackGroup extends AbstractLogback {
    private final LoggerContext context;
    private final LogbackProperties properties;

    public LogbackGroup(LoggerContext context, LogbackProperties properties) {
        this.context = context;
        this.properties = properties;
    }

    @Override
    public boolean supports(LogbackType logbackType) {
        return LogbackType.GROUP == logbackType;
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF &gt; ERROR &gt; WARN &gt; INFO &gt; DEBUG &gt; TRACE &gt;ALL
     *
     * @param field 上下文属性传递类
     * @return 日志对象
     */
    @Override
    public Logger getLogger(LogPathField field) {
        // 获取logger对象
        Logger logger = context.getLogger(field.getLoggerName());
        // 设置是否向上级打印信息
        logger.setAdditive(false);
        // 设置日志级别
        logger.setLevel(LevelUtil.levelStringToLevel(properties.getGroup().getLevel().toString()));
        // appender对象
        AbstractAppender appender = LogBeanFactory.getBean(LogbackRollingFileAppender.class).logPathField(field);
        // 是否开启异步日志
        if (properties.getAppender().getAsync().isEnabled()) {
            //异步appender
            LogbackAsyncAppender asyncAppender = LogBeanFactory.getBean(LogbackAsyncAppender.class);
            if (logger.getLevel().levelInt <= Level.ERROR_INT) {
                logger.addAppender(asyncAppender.getAppender(appender.build(Level.ERROR)));
            }
            if (logger.getLevel().levelInt <= Level.WARN_INT) {
                logger.addAppender(asyncAppender.getAppender(appender.build(Level.WARN)));
            }
            if (logger.getLevel().levelInt <= Level.INFO_INT) {
                logger.addAppender(asyncAppender.getAppender(appender.build(Level.INFO)));
            }
            if (logger.getLevel().levelInt <= Level.DEBUG_INT) {
                logger.addAppender(asyncAppender.getAppender(appender.build(Level.DEBUG)));
            }
            if (logger.getLevel().levelInt <= Level.TRACE_INT) {
                logger.addAppender(asyncAppender.getAppender(appender.build(Level.TRACE)));
            }
        } else {
            if (logger.getLevel().levelInt <= Level.ERROR_INT) {
                logger.addAppender(appender.build(Level.ERROR));
            }
            if (logger.getLevel().levelInt <= Level.WARN_INT) {
                logger.addAppender(appender.build(Level.WARN));
            }
            if (logger.getLevel().levelInt <= Level.INFO_INT) {
                logger.addAppender(appender.build(Level.INFO));
            }
            if (logger.getLevel().levelInt <= Level.DEBUG_INT) {
                logger.addAppender(appender.build(Level.DEBUG));
            }
            if (logger.getLevel().levelInt <= Level.TRACE_INT) {
                logger.addAppender(appender.build(Level.TRACE));
            }
        }
        if (properties.getGroup().isConsole()) {
            // 添加控制台appender
            logger.addAppender(LogBeanFactory.getBean(LogbackConsoleAppender.class).build(logger.getLevel()));
        }

        return logger;
    }
}
