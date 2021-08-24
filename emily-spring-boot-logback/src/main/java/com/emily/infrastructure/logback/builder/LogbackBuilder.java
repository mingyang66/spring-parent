package com.emily.infrastructure.logback.builder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.common.utils.constant.CharacterUtils;
import com.emily.infrastructure.common.utils.path.PathUtils;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.appender.LogbackConsoleAppender;
import com.emily.infrastructure.logback.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logback.appender.helper.LogbackAppender;
import com.emily.infrastructure.logback.enumeration.LogbackTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

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
public class LogbackBuilder extends AbstractLogbackBuilder {

    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    /**
     * Logger对象容器
     */
    private static final Map<String, Logger> loggerCache = new ConcurrentHashMap<>();

    private LogbackProperties properties;

    public LogbackBuilder(LogbackProperties properties) {
        builderRoot(properties);
        this.properties = properties;
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    public Logger getLogger(String path, String fileName) {
        return getLogger(path, fileName, false);
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName 日志文件名|模块名称
     * @param isModule 是否是模块|分组日志
     * @return
     */
    public Logger getLogger(String path, String fileName, boolean isModule) {
        // 日志文件路径
        path = PathUtils.normalizePath(path);
        //logger对象name
        String loggerName = StringUtils.join(path.replace(File.separator, CharacterUtils.LINE_THROUGH_BOTTOM), CharacterUtils.LINE_THROUGH_BOTTOM, fileName);
        Logger logger = loggerCache.get(loggerName);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (this) {
            logger = loggerCache.get(loggerName);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            logger = builder(loggerName, path, fileName, isModule);
            loggerCache.put(loggerName, logger);
        }
        return logger;

    }


    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    protected Logger builder(String name, String path, String fileName, boolean isModule) {
        if (isModule) {
            return builderModule(name, path, fileName);
        }
        return builderGroup(name, path, fileName);
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    @Override
    public Logger builderGroup(String name, String path, String fileName) {
        Logger logger = loggerContext.getLogger(name);
        /**
         * 设置是否向上级打印信息
         */
        logger.setAdditive(false);
        // 配置日志级别
        Level level = Level.toLevel(properties.getGroupLevel().levelStr);
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, properties);
        // 获取帮助类对象
        LogbackAppender logbackAppender = LogbackAppender.builder(name, path, fileName, LogbackTypeEnum.GROUP);
        //是否开启异步日志
        if (properties.isEnableAsyncAppender()) {
            LogbackAsyncAppender logbackAsyncAppender = new LogbackAsyncAppender(loggerContext, properties);
            if (level.levelInt <= Level.ERROR_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.ERROR))));
            }
            if (level.levelInt <= Level.WARN_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.WARN))));
            }
            if (level.levelInt <= Level.INFO_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.INFO))));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.DEBUG))));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.TRACE))));
            }
        } else {
            if (level.levelInt <= Level.ERROR_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.ERROR)));
            }
            if (level.levelInt <= Level.WARN_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.WARN)));
            }
            if (level.levelInt <= Level.INFO_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.INFO)));
            }
            if (level.levelInt <= Level.DEBUG_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.DEBUG)));
            }
            if (level.levelInt <= Level.TRACE_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.TRACE)));
            }
        }
        if (properties.isEnableGroupConsole()) {
            // 添加控制台appender
            logger.addAppender(new LogbackConsoleAppender(loggerContext, properties).getConsoleAppender(level));
        }
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
    @Override
    public Logger builderModule(String name, String path, String fileName) {
        Logger logger = loggerContext.getLogger(name);
        // 设置是否向上级打印信息
        logger.setAdditive(false);
        // 模块输出日志级别
        Level moduleLevel = Level.toLevel(properties.getModuleLevel().levelStr);
        LogbackRollingFileAppender rollingFileAppender = new LogbackRollingFileAppender(loggerContext, properties);
        // 获取帮助类对象
        LogbackAppender logbackAppender = LogbackAppender.builder(name, path, fileName, LogbackTypeEnum.MODULE);
        //是否开启异步日志
        if (properties.isEnableAsyncAppender()) {
            LogbackAsyncAppender logbackAsyncAppender = new LogbackAsyncAppender(loggerContext, properties);
            if (moduleLevel.levelInt == Level.ERROR_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.ERROR))));
            }
            if (moduleLevel.levelInt == Level.WARN_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.WARN))));
            }
            if (moduleLevel.levelInt == Level.INFO_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.INFO))));
            }
            if (moduleLevel.levelInt == Level.DEBUG_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.DEBUG))));
            }
            if (moduleLevel.levelInt == Level.TRACE_INT) {
                logger.addAppender(logbackAsyncAppender.getAsyncAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.TRACE))));
            }
        } else {
            if (moduleLevel.levelInt == Level.ERROR_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.ERROR)));
            }
            if (moduleLevel.levelInt == Level.WARN_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.WARN)));
            }
            if (moduleLevel.levelInt == Level.INFO_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.INFO)));
            }
            if (moduleLevel.levelInt == Level.DEBUG_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.DEBUG)));
            }
            if (moduleLevel.levelInt == Level.TRACE_INT) {
                logger.addAppender(rollingFileAppender.getRollingFileAppender(logbackAppender.builder(Level.TRACE)));
            }
        }
        if (properties.isEnableModuleConsole()) {
            // 添加控制台appender
            logger.addAppender(new LogbackConsoleAppender(loggerContext, properties).getConsoleAppender(moduleLevel));
        }
        // 设置日志级别
        logger.setLevel(moduleLevel);
        return logger;
    }
}
