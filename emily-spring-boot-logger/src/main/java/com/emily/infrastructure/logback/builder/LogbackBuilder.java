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
 * @description: 日志类 logback+slf4j
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
    public Logger getLogger(Class cls) {
        return getLogger(cls, null, null);
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    public Logger getLogger(Class cls, String path, String fileName) {
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
                logger = builder(cls, loggerName);
            } else {
                logger = builder(cls, loggerName, path, fileName);
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
    protected Logger builder(Class cls, String name) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(name);
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, properties);
        //设置是否向上级打印信息
        logger.setAdditive(false);
        // 配置日志级别
        Level level = Level.toLevel(properties.getLevel().levelStr);
        if (properties.isEnableAsyncAppender()) {
            LogbackAsyncAppender asyncAppender = new LogbackAsyncAppender(loggerContext, properties);
            if (level.levelInt <= Level.ERROR_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.ERROR.levelStr.toLowerCase(), Level.ERROR)));
            }
            if (level.levelInt <= Level.WARN_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.WARN.levelStr.toLowerCase(), Level.WARN)));
            }
            if (level.levelInt <= Level.INFO_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.INFO.levelStr.toLowerCase(), Level.INFO)));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.DEBUG.levelStr.toLowerCase(), Level.DEBUG)));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                logger.addAppender(asyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.TRACE.levelStr.toLowerCase(), Level.TRACE)));
            }
        } else {
            if (level.levelInt <= Level.ERROR_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.ERROR.levelStr.toLowerCase(), Level.ERROR));
            }
            if (level.levelInt <= Level.WARN_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.WARN.levelStr.toLowerCase(), Level.WARN));
            }
            if (level.levelInt <= Level.INFO_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.INFO.levelStr.toLowerCase(), Level.INFO));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.DEBUG.levelStr.toLowerCase(), Level.DEBUG));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, null, Level.TRACE.levelStr.toLowerCase(), Level.TRACE));
            }
        }
        // 添加控制台appender
        logger.addAppender(new LogbackConsoleAppender(loggerContext, properties).getConsoleAppender(level));
        // 设置日志级别
        logger.setLevel(level);
        return logger;
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    protected Logger builder(Class cls, String name, String path, String fileName) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(name);
        /**
         * 设置是否向上级打印信息
         */
        logger.setAdditive(false);
        // 配置日志级别
        Level level = Level.toLevel(properties.getLevel().levelStr);
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, properties);
        //是否开启异步日志
        if (properties.isEnableAsyncAppender()) {
            LogbackAsyncAppender logbackAsyncAppender = new LogbackAsyncAppender(loggerContext, properties);
            if (level.levelInt <= Level.ERROR_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.ERROR)));
            }
            if (level.levelInt <= Level.WARN_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.WARN)));
            }
            if (level.levelInt <= Level.INFO_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.INFO)));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.DEBUG)));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.TRACE)));
            }
        } else {
            if (level.levelInt <= Level.ERROR_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.ERROR));
            }
            if (level.levelInt <= Level.WARN_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.WARN));
            }
            if (level.levelInt <= Level.INFO_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.INFO));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.DEBUG));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.TRACE));
            }
        }
        if (properties.isEnableModuleConsole()) {
            // 添加控制台appender
            logger.addAppender(new LogbackConsoleAppender(loggerContext, properties).getConsoleAppender(level));
        }
        // 设置日志级别
        logger.setLevel(level);
        return logger;
    }


}
