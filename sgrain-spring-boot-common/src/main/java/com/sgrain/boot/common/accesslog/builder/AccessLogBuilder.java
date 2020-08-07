package com.sgrain.boot.common.accesslog.builder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.sgrain.boot.common.accesslog.appender.AccessLogConsoleAppender;
import com.sgrain.boot.common.accesslog.appender.AccessLogRollingFileAppender;
import com.sgrain.boot.common.accesslog.level.AccessLogLevel;
import com.sgrain.boot.common.accesslog.po.AccessLog;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: spring-parent
 * @description: 日志类
 * @create: 2020/08/04
 */
public class AccessLogBuilder {
    /**
     * Logger对象容器
     */
    private Map<String, Logger> loggerCache;

    private AccessLog accessLog;

    public AccessLogBuilder(AccessLog accessLog) {
        this.loggerCache = new ConcurrentHashMap<>();
        this.accessLog = accessLog;
    }

    /**
     * 获取日志输出对象
     *
     * @return
     */
    public Logger getLogger(Class<?> clazz) {
        return getLogger(clazz, AccessLog.DEFAULT_MODULE);
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    public Logger getLogger(Class<?> cls, String fileName) {
        /**
         * 判定是否是默认文件名
         */
        boolean defaultBool = StringUtils.equals(AccessLog.DEFAULT_MODULE, fileName);
        if (defaultBool) {
            fileName = cls.getName();
        }
        Logger logger = loggerCache.get(fileName);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (AccessLogBuilder.class) {
            logger = loggerCache.get(fileName);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            if (defaultBool) {
                logger = builder(cls);
                loggerCache.put(cls.getName(), logger);
            } else {
                logger = builder(cls, fileName);
                loggerCache.put(fileName, logger);
            }
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

        //设置是否向上级打印信息
        logger.setAdditive(false);
        logger.addAppender(new AccessLogRollingFileAppender(loggerContext, accessLog).getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.ERROR.levelStr), Level.ERROR));
        logger.addAppender(new AccessLogRollingFileAppender(loggerContext, accessLog).getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.WARN.levelStr), Level.WARN));
        logger.addAppender(new AccessLogRollingFileAppender(loggerContext, accessLog).getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.INFO.levelStr), Level.INFO));
        logger.addAppender(new AccessLogRollingFileAppender(loggerContext, accessLog).getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.DEBUG.levelStr), Level.DEBUG));
        logger.addAppender(new AccessLogRollingFileAppender(loggerContext, accessLog).getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.TRACE.levelStr), Level.TRACE));
        logger.addAppender(new AccessLogRollingFileAppender(loggerContext, accessLog).getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.ALL.levelStr), Level.ALL));
        logger.addAppender(new AccessLogConsoleAppender(loggerContext, accessLog).getConsoleAppender(AccessLogLevel.getLogLevel(accessLog.getLevel())));

        logger.setLevel(AccessLogLevel.getLogLevel(accessLog.getLevel()));
        return logger;
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    private Logger builder(Class<?> cls, String fileName) {
        //logger 属性namge名称
        String name = StringUtils.join(cls.getName(), CharacterUtils.LINE_THROUGH_BOTTOM, fileName);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(name);
        /**
         * 设置是否向上级打印信息
         */
        logger.setAdditive(false);
        logger.addAppender(new AccessLogRollingFileAppender(loggerContext, accessLog).getRollingFileApender(name, fileName, Level.INFO));
        if(accessLog.isEnableModuleConsule()){
            logger.addAppender(new AccessLogConsoleAppender(loggerContext, accessLog).getConsoleAppender(AccessLogLevel.getLogLevel(accessLog.getLevel())));
        }

        logger.setLevel(AccessLogLevel.getLogLevel(accessLog.getLevel()));
        return logger;
    }


}
