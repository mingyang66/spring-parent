package com.emily.infrastructure.logger.configuration.context;

import com.emily.infrastructure.logger.common.PathUtils;
import com.emily.infrastructure.logger.configuration.classic.Logback;
import com.emily.infrastructure.logger.configuration.classic.LogbackGroup;
import com.emily.infrastructure.logger.configuration.classic.LogbackModule;
import com.emily.infrastructure.logger.configuration.property.LogbackAppender;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.LogbackType;
import com.emily.infrastructure.logger.manager.CacheManager;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 日志类 logback+slf4j
 * @create: 2020/08/04
 */
public class LogbackContext {

    private final LoggerProperties properties;

    public LogbackContext(LoggerProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName    日志文件名|模块名称
     * @param logbackType 日志类别 {@link LogbackType}
     */
    public <T> Logger getLogger(Class<T> clazz, String filePath, String fileName, LogbackType logbackType) {
        LogbackAppender appender = new LogbackAppender();
        // 获取缓存key
        appender.setAppenderName(getAppenderName(filePath, fileName, logbackType));
        // 文件保存路径
        appender.setFilePath(filePath);
        // 文件名
        appender.setFileName(fileName);
        // 日志类型
        appender.setLogbackType(logbackType);
        //获取loggerName
        String loggerName = getLoggerName(clazz, appender.getAppenderName());
        // 获取Logger对象
        Logger logger = CacheManager.LOGGER.get(loggerName);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (this) {
            logger = CacheManager.LOGGER.get(loggerName);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            //获取logger日志对象
            logger = getLogger(loggerName, appender);
            //存入缓存
            CacheManager.LOGGER.put(loggerName, logger);
        }
        return logger;
    }


    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param loggerName logger name
     * @param appender appender instance object
     */
    protected Logger getLogger(String loggerName, LogbackAppender appender) {
        Logback logback;
        if (appender.getLogbackType().equals(LogbackType.MODULE)) {
            logback = new LogbackModule(this.properties);
        } else {
            logback = new LogbackGroup(this.properties);
        }
        return logback.getLogger(loggerName, appender);
    }

    /**
     * 获取appenderName
     *
     * @param clazz        当前类实例
     * @param appenderName appender属性名
     * @return appenderName
     */
    private <T> String getLoggerName(Class<T> clazz, String appenderName) {
        return MessageFormat.format("{0}.{1}", appenderName, clazz.getName());
    }

    /**
     * @param filePath    路径
     * @param fileName    文件名
     * @param logbackType 类型
     */
    private String getAppenderName(String filePath, String fileName, LogbackType logbackType) {
        return MessageFormat.format("{0}{1}.{2}", filePath, fileName, logbackType.getCode()).replace(PathUtils.SLASH, PathUtils.DOT);
    }

    /**
     * 清空保存的日志对象
     */
    public void clear() {
        CacheManager.LOGGER.clear();
    }
}
