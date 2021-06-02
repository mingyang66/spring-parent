package com.emily.infrastructure.logback.builder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.rolling.RollingFileAppender;
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
    public static final String LOGGER_NAME = "EMILY_LOGGER";
    private static LogbackProperties properties;

    public static void setAccessLog(LogbackProperties properties) {
        LogbackBuilder.properties = properties;
    }

    /**
     * 获取日志输出对象
     *
     * @return
     */
    public static Logger getLogger() {
        return getLogger(null, null);
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    public static Logger getLogger(String path, String fileName) {
        String name;
        /**
         * 判定是否是默认文件名
         */
        boolean defaultBool = !StringUtils.hasLength(path) && !StringUtils.hasLength(fileName);
        if (defaultBool) {
            name = LOGGER_NAME;
        } else {
            name = String.join(File.separator, path, fileName);
        }
        Logger logger = loggerCache.get(name);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (LogbackBuilder.class) {
            logger = loggerCache.get(name);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            if (defaultBool) {
                logger = builder(name);
            } else {
                logger = builder(name, path, fileName);
            }
            loggerCache.put(name, logger);
        }
        return logger;

    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @return
     */
    private static Logger builder(String name) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger logger = loggerContext.getLogger(name);
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, properties);
        RollingFileAppender appenderError = rollingFileAppender.getRollingFileAppender(name, Level.ERROR.levelStr.toLowerCase(), Level.ERROR.levelStr.toLowerCase(), Level.ERROR);
        RollingFileAppender appenderWarn = rollingFileAppender.getRollingFileAppender(name, Level.WARN.levelStr.toLowerCase(), Level.WARN.levelStr.toLowerCase(), Level.WARN);
        RollingFileAppender appenderInfo = rollingFileAppender.getRollingFileAppender(name, Level.INFO.levelStr.toLowerCase(), Level.INFO.levelStr.toLowerCase(), Level.INFO);
        RollingFileAppender appenderDebug = rollingFileAppender.getRollingFileAppender(name, Level.DEBUG.levelStr.toLowerCase(), Level.DEBUG.levelStr.toLowerCase(), Level.DEBUG);
        RollingFileAppender appenderTrace = rollingFileAppender.getRollingFileAppender(name, Level.TRACE.levelStr.toLowerCase(), Level.TRACE.levelStr.toLowerCase(), Level.TRACE);
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
        logger.addAppender(new LogbackConsoleAppender(loggerContext, properties).getConsoleAppender(Level.toLevel(properties.getLevel())));

        logger.setLevel(Level.toLevel(properties.getLevel()));
        return logger;
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    private static Logger builder(String name, String path, String fileName) {
        if (StringUtils.hasLength(path)) {
            // 去除字符串开头斜杠/
            path = path.startsWith(File.separator) ? path.substring(1) : path;
            // 去除字符串末尾斜杠/
            path = path.endsWith(File.separator) ? path.substring(0, path.length() - 1) : path;
        }
        //logger 属性name名称
        //String name = String.join(".", name, path, fileName);
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
        if (properties.isEnableModuleConsule()) {
            logger.addAppender(new LogbackConsoleAppender(loggerContext, properties).getConsoleAppender(Level.toLevel(properties.getLevel())));
        }

        logger.setLevel(Level.toLevel(properties.getLevel()));
        return logger;
    }


}
