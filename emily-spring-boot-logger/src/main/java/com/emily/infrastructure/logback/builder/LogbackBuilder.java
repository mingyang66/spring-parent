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
    /**
     * 横线
     */
    private static final String CROSS_LINE = "-";

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
        if (determineDefaultLoggerName(path, fileName)) {
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
            if (determineDefaultLoggerName(path, fileName)) {
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
    protected boolean determineDefaultLoggerName(String path, String fileName) {
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
        RollingFileAppender appenderError = rollingFileAppender.getRollingFileAppender(name, PathUtils.normalizePath(Level.ERROR.levelStr.toLowerCase()), Level.ERROR.levelStr.toLowerCase(), Level.ERROR);
        RollingFileAppender appenderWarn = rollingFileAppender.getRollingFileAppender(name, PathUtils.normalizePath(Level.WARN.levelStr.toLowerCase()), Level.WARN.levelStr.toLowerCase(), Level.WARN);
        RollingFileAppender appenderInfo = rollingFileAppender.getRollingFileAppender(name, PathUtils.normalizePath(Level.INFO.levelStr.toLowerCase()), Level.INFO.levelStr.toLowerCase(), Level.INFO);
        RollingFileAppender appenderDebug = rollingFileAppender.getRollingFileAppender(name, PathUtils.normalizePath(Level.DEBUG.levelStr.toLowerCase()), Level.DEBUG.levelStr.toLowerCase(), Level.DEBUG);
        RollingFileAppender appenderTrace = rollingFileAppender.getRollingFileAppender(name, PathUtils.normalizePath(Level.TRACE.levelStr.toLowerCase()), Level.TRACE.levelStr.toLowerCase(), Level.TRACE);
        //设置是否向上级打印信息
        logger.setAdditive(false);
        if (properties.isEnableAsyncAppender()) {
            logger.addAppender(new LogbackAsyncAppender(loggerContext, properties).getAsyncAppender(appenderError));
            logger.addAppender(new LogbackAsyncAppender(loggerContext, properties).getAsyncAppender(appenderWarn));
            logger.addAppender(new LogbackAsyncAppender(loggerContext, properties).getAsyncAppender(appenderInfo));
            logger.addAppender(new LogbackAsyncAppender(loggerContext, properties).getAsyncAppender(appenderDebug));
            logger.addAppender(new LogbackAsyncAppender(loggerContext, properties).getAsyncAppender(appenderTrace));
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
        RollingFileAppender rollingFileAppenderInfo = rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.INFO);
        Logger logger = loggerContext.getLogger(name);
        /**
         * 设置是否向上级打印信息
         */
        logger.setAdditive(false);
        //是否开启异步日志
        if (properties.isEnableAsyncAppender()) {
            logger.addAppender(new LogbackAsyncAppender(loggerContext, properties).getAsyncAppender(rollingFileAppenderInfo));
        } else {
            logger.addAppender(rollingFileAppenderInfo);
        }
        if (properties.isEnableModuleConsole()) {
            logger.addAppender(new LogbackConsoleAppender(loggerContext, properties).getConsoleAppender(Level.toLevel(properties.getLevel().levelStr)));
        }

        logger.setLevel(Level.toLevel(properties.getLevel().levelStr));
        return logger;
    }


}
