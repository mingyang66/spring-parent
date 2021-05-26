package com.emily.infrastructure.autoconfigure.logger.common.builder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.emily.infrastructure.autoconfigure.logger.common.appender.LogbackAsyncAppender;
import com.emily.infrastructure.autoconfigure.logger.common.appender.LogbackConsoleAppender;
import com.emily.infrastructure.autoconfigure.logger.common.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.autoconfigure.logger.common.properties.Logback;
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
    private Map<String, Logger> loggerCache;

    private Logback accessLog;

    public LogbackBuilder(Logback accessLog) {
        this.loggerCache = new ConcurrentHashMap<>();
        this.accessLog = accessLog;
    }

    /**
     * 获取日志输出对象
     *
     * @return
     */
    public Logger getLogger(Class<?> clazz) {
        return getLogger(clazz, null, null);
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    public Logger getLogger(Class<?> cls, String path, String fileName) {
        /**
         * 判定是否是默认文件名
         */
        boolean defaultBool = !StringUtils.hasLength(path) && !StringUtils.hasLength(fileName);
        String key;
        if (defaultBool) {
            key = cls.getName();
        } else {
            key = String.join(File.separator, path, fileName);
        }
        Logger logger = loggerCache.get(key);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (LogbackBuilder.class) {
            logger = loggerCache.get(key);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            if (defaultBool) {
                logger = builder(cls);
            } else {
                logger = builder(cls, path, fileName);
            }
            loggerCache.put(key, logger);
        }
        return logger;

    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @return
     */
    private Logger builder(Class<?> cls) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(cls.getName());
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, accessLog);
        RollingFileAppender appenderError = rollingFileAppender.getRollingFileAppender(cls.getName(), Level.ERROR.levelStr.toLowerCase(), Level.ERROR.levelStr.toLowerCase(), Level.ERROR);
        RollingFileAppender appenderWarn = rollingFileAppender.getRollingFileAppender(cls.getName(), Level.WARN.levelStr.toLowerCase(), Level.WARN.levelStr.toLowerCase(), Level.WARN);
        RollingFileAppender appenderInfo = rollingFileAppender.getRollingFileAppender(cls.getName(), Level.INFO.levelStr.toLowerCase(), Level.INFO.levelStr.toLowerCase(), Level.INFO);
        RollingFileAppender appenderDebug = rollingFileAppender.getRollingFileAppender(cls.getName(), Level.DEBUG.levelStr.toLowerCase(), Level.DEBUG.levelStr.toLowerCase(), Level.DEBUG);
        RollingFileAppender appenderTrace = rollingFileAppender.getRollingFileAppender(cls.getName(), Level.TRACE.levelStr.toLowerCase(), Level.TRACE.levelStr.toLowerCase(), Level.TRACE);
        RollingFileAppender appenderAll = rollingFileAppender.getRollingFileAppender(cls.getName(), Level.ALL.levelStr.toLowerCase(), Level.ALL.levelStr.toLowerCase(), Level.ALL);
        //设置是否向上级打印信息
        logger.setAdditive(false);
        if(accessLog.isEnableAsyncAppender()){
            logger.addAppender(new LogbackAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderError));
            logger.addAppender(new LogbackAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderWarn));
            logger.addAppender(new LogbackAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderInfo));
            logger.addAppender(new LogbackAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderDebug));
            logger.addAppender(new LogbackAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderTrace));
            logger.addAppender(new LogbackAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderAll));
        } else {
            logger.addAppender(appenderError);
            logger.addAppender(appenderWarn);
            logger.addAppender(appenderInfo);
            logger.addAppender(appenderDebug);
            logger.addAppender(appenderTrace);
            logger.addAppender(appenderAll);
        }
        logger.addAppender(new LogbackConsoleAppender(loggerContext, accessLog).getConsoleAppender(Level.toLevel(accessLog.getLevel())));

        logger.setLevel(Level.toLevel(accessLog.getLevel()));
        return logger;
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    private Logger builder(Class<?> cls, String path, String fileName) {
        if(StringUtils.hasLength(path)){
            // 去除字符串开头斜杠/
            path = path.startsWith(File.separator) ? path.substring(1) : path;
            // 去除字符串末尾斜杠/
            path = path.endsWith(File.separator) ? path.substring(0, path.length()-1) : path;
        }
        //logger 属性name名称
        String name = String.join(".", cls.getName(), path, fileName);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, accessLog);
        //获取Info对应的appender对象
        RollingFileAppender rollingFileAppenderInfo = rollingFileAppender.getRollingFileAppender(name, path, fileName, Level.INFO);
        Logger logger = loggerContext.getLogger(name);
        /**
         * 设置是否向上级打印信息
         */
        logger.setAdditive(false);
        //是否开启异步日志
        if(accessLog.isEnableAsyncAppender()){
            logger.addAppender(new LogbackAsyncAppender(loggerContext, accessLog).getAsyncAppender(rollingFileAppenderInfo));
        } else {
            logger.addAppender(rollingFileAppenderInfo);
        }
        if(accessLog.isEnableModuleConsule()){
            logger.addAppender(new LogbackConsoleAppender(loggerContext, accessLog).getConsoleAppender(Level.toLevel(accessLog.getLevel())));
        }

        logger.setLevel(Level.toLevel(accessLog.getLevel()));
        return logger;
    }


}
