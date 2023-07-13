package com.emily.infrastructure.logger.configuration.context;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.common.PathUtils;
import com.emily.infrastructure.logger.configuration.classic.AbstractLogback;
import com.emily.infrastructure.logger.configuration.classic.LogbackGroup;
import com.emily.infrastructure.logger.configuration.classic.LogbackModule;
import com.emily.infrastructure.logger.configuration.classic.LogbackRoot;
import com.emily.infrastructure.logger.configuration.property.LogbackAppender;
import com.emily.infrastructure.logger.configuration.property.LogbackAppenderBuilder;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.LogbackType;
import com.emily.infrastructure.logger.manager.LoggerCacheManager;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 日志类 logback+slf4j
 * @create: 2020/08/04
 */
public class LogbackContext implements Context {
    private LoggerProperties properties;
    private LoggerContext loggerContext;

    /**
     * 属性配置
     *
     * @param properties logback日志属性
     * @param context    上下文
     */
    @Override
    public void configure(LoggerProperties properties, LoggerContext context) {
        this.properties = properties;
        this.loggerContext = context;
    }

    /**
     * 获取logger日志对象
     *
     * @param clazz       当前打印类实例
     * @param filePath    文件路径
     * @param fileName    文件名称
     * @param logbackType 日志类型
     * @param <T>         类类型
     * @return logger对象
     */
    @Override
    public <T> Logger getLogger(Class<T> clazz, String filePath, String fileName, LogbackType logbackType) {
        LogbackAppender appender = new LogbackAppenderBuilder()
                // 获取缓存key
                .withAppenderName(getAppenderName(filePath, fileName, logbackType))
                // 文件保存路径
                .withFilePath(filePath)
                // 文件名
                .withFileName(fileName)
                // 日志类型
                .withLogbackType(logbackType)
                .build();
        //获取loggerName
        String loggerName = getLoggerName(clazz, appender.getAppenderName());
        // 获取Logger对象
        Logger logger = LoggerCacheManager.LOGGER.get(loggerName);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (this) {
            logger = LoggerCacheManager.LOGGER.get(loggerName);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            //获取logger日志对象
            logger = getLogger(loggerName, appender);
            //存入缓存
            LoggerCacheManager.LOGGER.put(loggerName, logger);
        }
        return logger;
    }


    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param loggerName logger name
     * @param appender   appender instance object
     */
    protected Logger getLogger(String loggerName, LogbackAppender appender) {
        AbstractLogback logback;
        if (appender.getLogbackType().equals(LogbackType.MODULE)) {
            logback = new LogbackModule(properties, loggerContext);
        } else if (appender.getLogbackType().equals(LogbackType.GROUP)) {
            logback = new LogbackGroup(properties, loggerContext);
        } else {
            logback = new LogbackRoot(properties, loggerContext);
        }
        return logback.getLogger(loggerName, appender);
    }

    /**
     * 获取 logger name
     *
     * @param clazz        当前类实例
     * @param appenderName appender属性名
     * @return logger name
     */
    private <T> String getLoggerName(Class<T> clazz, String appenderName) {
        return MessageFormat.format("{0}.{1}", appenderName, clazz.getName());
    }

    /**
     * 获取appenderName
     *
     * @param filePath    路径
     * @param fileName    文件名
     * @param logbackType 类型
     * @return appender name
     */
    private String getAppenderName(String filePath, String fileName, LogbackType logbackType) {
        return MessageFormat.format("{0}{1}.{2}", filePath, fileName, logbackType.getCode()).replace(PathUtils.SLASH, PathUtils.DOT);
    }

    /**
     * 启动上下文，初始化root logger对象
     */
    @Override
    public void start() {
        // 初始化root logger
        LogbackAppender appender = new LogbackAppenderBuilder()
                // appender name
                .withAppenderName(Logger.ROOT_LOGGER_NAME)
                // logger file path
                .withFilePath(properties.getRoot().getFilePath())
                // logger type
                .withLogbackType(LogbackType.ROOT)
                .build();
        // 获取root logger对象
        Logger rootLogger = getLogger(Logger.ROOT_LOGGER_NAME, appender);
        // 将root添加到缓存
        LoggerCacheManager.LOGGER.put(Logger.ROOT_LOGGER_NAME, rootLogger);
    }

    /**
     * 此方法会清除掉所有的内部属性，内部状态消息除外，关闭所有的appender，移除所有的turboFilters过滤器，
     * 引发OnReset事件，移除所有的状态监听器，移除所有的上下文监听器（reset相关复位除外）
     */
    @Override
    public void stopAndReset() {
        loggerContext.stop();
        loggerContext.reset();
        LoggerCacheManager.LOGGER.clear();
        LoggerCacheManager.APPENDER.clear();
    }
}
