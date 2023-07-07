package com.emily.infrastructure.logger.configuration.context;

import com.emily.infrastructure.logger.configuration.classic.Logback;
import com.emily.infrastructure.logger.configuration.classic.LogbackGroup;
import com.emily.infrastructure.logger.configuration.classic.LogbackModule;
import com.emily.infrastructure.logger.configuration.classic.LogbackRoot;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.LogbackType;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 日志类 logback+slf4j
 * @create: 2020/08/04
 */
public class LogbackContext {
    /**
     * Logger对象容器
     */
    private final Map<String, Logger> cacheLogger = new ConcurrentHashMap<>();

    private final LoggerProperties properties;
    private final ch.qos.logback.classic.Logger root;

    public LogbackContext(LoggerProperties properties) {
        this.properties = properties;
        this.root = new LogbackRoot(properties).getLogger();
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName    日志文件名|模块名称
     * @param logbackType 日志类别 {@link LogbackType}
     */
    public <T> Logger getLogger(Class<T> clazz, String filePath, String fileName, LogbackType logbackType) {
        // 获取缓存key
        String appenderName = getAppenderName(filePath, fileName, logbackType);
        //获取loggerName
        String loggerName = getLoggerName(clazz, appenderName);
        // 获取Logger对象
        Logger logger = cacheLogger.get(loggerName);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (this) {
            logger = cacheLogger.get(loggerName);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            //获取logger日志对象
            logger = getLogger(loggerName, appenderName, filePath, fileName, logbackType);
            //存入缓存
            cacheLogger.put(loggerName, logger);
        }
        return logger;
    }


    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     */
    protected Logger getLogger(String loggerName, String appenderName, String filePath, String fileName, LogbackType logbackType) {
        Logback logback;
        if (logbackType.getType().equals(LogbackType.MODULE.getType())) {
            logback = new LogbackModule(this.properties);
        } else {
            logback = new LogbackGroup(this.properties);
        }
        return logback.getLogger(loggerName, appenderName, filePath, fileName);
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
        return MessageFormat.format("{0}{1}.{2}", filePath, fileName, logbackType.getType()).replace("/", ".");
    }

    /**
     * 清空保存的日志对象
     */
    public void clear() {
        this.cacheLogger.clear();
    }
}
