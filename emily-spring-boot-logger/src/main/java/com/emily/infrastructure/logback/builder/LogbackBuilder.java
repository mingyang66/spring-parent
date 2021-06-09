package com.emily.infrastructure.logback.builder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.emily.infrastructure.common.utils.path.PathUtils;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.appender.LogbackConsoleAppender;
import com.emily.infrastructure.logback.appender.LogbackRollingFileAppender;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 日志类
 * @create: 2020/08/04
 */
public class LogbackBuilder {
    /**
     * Logger对象容器
     */
    private static final Map<String, Logger> loggerCache = new ConcurrentHashMap<>();
    /**
     * 日志文件名
     */
    private static final String LOGGER_NAME = "EMILY_LOGGER";

    private LogbackProperties properties;

    public LogbackBuilder(LogbackProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取日志输出对象
     *
     * @return
     */
    public Logger getLogger() {
        return getLogger(null, null);
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    public Logger getLogger(String path, String fileName) {
        /**
         * 路径地址标准化
         */
        path = PathUtils.normalizePath(path);
        /**
         * logger对象name
         */
        String loggerName;
        if (isDefaultLoggerName(path, fileName)) {
            loggerName = LOGGER_NAME;
        } else {
            loggerName = String.join(File.separator, path, fileName);
        }
        Logger logger = loggerCache.get(loggerName);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (this) {
            logger = loggerCache.get(loggerName);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            if (isDefaultLoggerName(path, fileName)) {
                logger = builder(loggerName);
            } else {
                logger = builder(loggerName, path, fileName);
            }
            loggerCache.put(loggerName, logger);
        }
        return logger;

    }

    /**
     * 判定是否是默认Logger对象名称
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @return 默认logger对象名 true
     */
    protected boolean isDefaultLoggerName(String path, String fileName) {
        return !(StringUtils.hasLength(path) && StringUtils.hasLength(fileName));
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @return
     */
    protected Logger builder(String name) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger logger = loggerContext.getLogger(name);
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, properties);
        RollingFileAppender appenderError = rollingFileAppender.getRollingFileAppender(name, null, Level.ERROR.levelStr.toLowerCase(), Level.ERROR);
        RollingFileAppender appenderWarn = rollingFileAppender.getRollingFileAppender(name, null, Level.WARN.levelStr.toLowerCase(), Level.WARN);
        RollingFileAppender appenderInfo = rollingFileAppender.getRollingFileAppender(name, null, Level.INFO.levelStr.toLowerCase(), Level.INFO);
        RollingFileAppender appenderDebug = rollingFileAppender.getRollingFileAppender(name, null, Level.DEBUG.levelStr.toLowerCase(), Level.DEBUG);
        RollingFileAppender appenderTrace = rollingFileAppender.getRollingFileAppender(name, null, Level.TRACE.levelStr.toLowerCase(), Level.TRACE);
        //设置是否向上级打印信息
        logger.setAdditive(false);
        if (properties.isEnableAsyncAppender()) {
            LogbackAsyncAppender asyncAppender = new LogbackAsyncAppender(loggerContext, properties);
            logger.addAppender(asyncAppender.getAsyncAppender(appenderError));
            logger.addAppender(asyncAppender.getAsyncAppender(appenderWarn));
            logger.addAppender(asyncAppender.getAsyncAppender(appenderInfo));
            logger.addAppender(asyncAppender.getAsyncAppender(appenderDebug));
            logger.addAppender(asyncAppender.getAsyncAppender(appenderTrace));
        } else {
            logger.addAppender(appenderError);
            logger.addAppender(appenderWarn);
            logger.addAppender(appenderInfo);
            logger.addAppender(appenderDebug);
            logger.addAppender(appenderTrace);
        }
        logger.addAppender(new LogbackConsoleAppender(loggerContext, properties).getConsoleAppender(Level.toLevel(properties.getLevel().levelStr)));

        logger.setLevel(Level.toLevel(properties.getLevel().levelStr));
        return logger;
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    protected Logger builder(String name, String path, String fileName) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, properties);
        //获取Info对应的appender对象
        RollingFileAppender rollingFileAppenderError = rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.ERROR);
        RollingFileAppender rollingFileAppenderWarn = rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.WARN);
        RollingFileAppender rollingFileAppenderInfo = rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.INFO);
        RollingFileAppender rollingFileAppenderDebug = rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.DEBUG);
        RollingFileAppender rollingFileAppenderTrace = rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.TRACE);
        Logger logger = loggerContext.getLogger(name);
        /**
         * 设置是否向上级打印信息
         */
        logger.setAdditive(false);
        //是否开启异步日志
        if (properties.isEnableAsyncAppender()) {
            LogbackAsyncAppender logbackAsyncAppender = new LogbackAsyncAppender(loggerContext, properties);
            logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppenderError));
            logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppenderWarn));
            logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppenderInfo));
            logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppenderDebug));
            logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppenderTrace));
        } else {
            logger.addAppender(rollingFileAppenderError);
            logger.addAppender(rollingFileAppenderWarn);
            logger.addAppender(rollingFileAppenderInfo);
            logger.addAppender(rollingFileAppenderDebug);
            logger.addAppender(rollingFileAppenderTrace);
        }
        if (properties.isEnableModuleConsole()) {
            logger.addAppender(new LogbackConsoleAppender(loggerContext, properties).getConsoleAppender(Level.toLevel(properties.getLevel().levelStr)));
        }

        logger.setLevel(Level.toLevel(properties.getLevel().levelStr));
        return logger;
    }


}
