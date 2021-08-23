package com.emily.infrastructure.logback.builder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logback.appender.helper.LogbackAppender;
import com.emily.infrastructure.logback.enumeration.LogbackTypeEnum;
import org.slf4j.LoggerFactory;

/**
 * @program: spring-parent
 * @description: 日志组件抽象类
 * @author: Emily
 * @create: 2021/07/08
 */
public abstract class AbstractLogbackBuilder {

    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    /**
     * 构建RootLogger对象，需在配置类中主动调用进行初始化
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     */
    public void builderRoot(LogbackProperties properties) {
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, properties);
        // 获取帮助类对象
        LogbackAppender logbackAppender = LogbackAppender.builder(Logger.ROOT_LOGGER_NAME, null, null, LogbackTypeEnum.ROOT);
        // 配置日志级别
        Level level = Level.toLevel(properties.getLevel().levelStr);
        // 设置日志级别
        rootLogger.setLevel(level);
        //设置是否向上级打印信息
        rootLogger.setAdditive(false);
        if (properties.isEnableAsyncAppender()) {
            LogbackAsyncAppender asyncAppender = new LogbackAsyncAppender(loggerContext, properties);
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
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    public abstract Logger builderGroup(String name, String path, String fileName);

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    public abstract Logger builderModule(String name, String path, String fileName);
}
