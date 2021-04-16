package com.emily.framework.common.logger.builder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.emily.framework.common.logger.appender.AccessLogAsyncAppender;
import com.emily.framework.common.logger.appender.AccessLogConsoleAppender;
import com.emily.framework.common.logger.appender.AccessLogRollingFileAppender;
import com.emily.framework.common.logger.level.AccessLogLevel;
import com.emily.framework.common.logger.po.AccessLog;
import com.emily.framework.common.utils.constant.CharacterUtils;
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
        boolean defaultBool = StringUtils.isEmpty(path) && StringUtils.isEmpty(fileName);
        String key;
        if (defaultBool) {
            key = cls.getName();
        } else {
            key = String.join(CharacterUtils.LINE_THROUGH_CENTER, path, fileName);
        }
        Logger logger = loggerCache.get(key);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (AccessLogBuilder.class) {
            logger = loggerCache.get(key);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            if (defaultBool) {
                logger = builder(cls);
                loggerCache.put(key, logger);
            } else {
                logger = builder(cls, path, fileName);
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
        AccessLogRollingFileAppender rollingFileAppender = new AccessLogRollingFileAppender(loggerContext, accessLog);
        RollingFileAppender appenderError = rollingFileAppender.getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.ERROR.levelStr), StringUtils.lowerCase(Level.ERROR.levelStr), Level.ERROR);
        RollingFileAppender appenderWarn = rollingFileAppender.getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.WARN.levelStr), StringUtils.lowerCase(Level.WARN.levelStr), Level.WARN);
        RollingFileAppender appenderInfo = rollingFileAppender.getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.INFO.levelStr), StringUtils.lowerCase(Level.INFO.levelStr), Level.INFO);
        RollingFileAppender appenderDebug = rollingFileAppender.getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.DEBUG.levelStr), StringUtils.lowerCase(Level.DEBUG.levelStr), Level.DEBUG);
        RollingFileAppender appenderTrace = rollingFileAppender.getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.TRACE.levelStr), StringUtils.lowerCase(Level.TRACE.levelStr), Level.TRACE);
        RollingFileAppender appenderAll = rollingFileAppender.getRollingFileApender(cls.getName(), StringUtils.lowerCase(Level.ALL.levelStr), StringUtils.lowerCase(Level.ALL.levelStr), Level.ALL);
        //设置是否向上级打印信息
        logger.setAdditive(false);
        if(accessLog.isEnableAsyncAppender()){
            logger.addAppender(new AccessLogAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderError));
            logger.addAppender(new AccessLogAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderWarn));
            logger.addAppender(new AccessLogAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderInfo));
            logger.addAppender(new AccessLogAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderDebug));
            logger.addAppender(new AccessLogAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderTrace));
            logger.addAppender(new AccessLogAsyncAppender(loggerContext, accessLog).getAsyncAppender(appenderAll));
        } else {
            logger.addAppender(appenderError);
            logger.addAppender(appenderWarn);
            logger.addAppender(appenderInfo);
            logger.addAppender(appenderDebug);
            logger.addAppender(appenderTrace);
            logger.addAppender(appenderAll);
        }
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
    private Logger builder(Class<?> cls, String path, String fileName) {
        if(StringUtils.isNotEmpty(path)){
            // 去除字符串开头斜杠/
            path = StringUtils.removeStart(path, CharacterUtils.PATH_SEPARATOR);
            // 去除字符串末尾斜杠/
            path = StringUtils.removeEnd(path, CharacterUtils.PATH_SEPARATOR);
        }
        //logger 属性namge名称
        String name = StringUtils.join(cls.getName(), CharacterUtils.LINE_THROUGH_BOTTOM, fileName);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        AccessLogRollingFileAppender rollingFileAppender = new AccessLogRollingFileAppender(loggerContext, accessLog);
        //获取Info对应的appender对象
        RollingFileAppender rollingFileAppenderInfo = rollingFileAppender.getRollingFileApender(name, path, fileName, Level.INFO);
        Logger logger = loggerContext.getLogger(name);
        /**
         * 设置是否向上级打印信息
         */
        logger.setAdditive(false);
        //是否开启异步日志
        if(accessLog.isEnableAsyncAppender()){
            logger.addAppender(new AccessLogAsyncAppender(loggerContext, accessLog).getAsyncAppender(rollingFileAppenderInfo));
        } else {
            logger.addAppender(rollingFileAppenderInfo);
        }
        if(accessLog.isEnableModuleConsule()){
            logger.addAppender(new AccessLogConsoleAppender(loggerContext, accessLog).getConsoleAppender(AccessLogLevel.getLogLevel(accessLog.getLevel())));
        }

        logger.setLevel(AccessLogLevel.getLogLevel(accessLog.getLevel()));
        return logger;
    }


}
