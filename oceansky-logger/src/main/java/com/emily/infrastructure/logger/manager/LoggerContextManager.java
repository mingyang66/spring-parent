package com.emily.infrastructure.logger.manager;

import ch.qos.logback.classic.Logger;
import com.emily.infrastructure.logger.configuration.classic.LogbackRoot;
import com.emily.infrastructure.logger.configuration.context.LogbackContext;
import com.emily.infrastructure.logger.configuration.property.LogbackAppender;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.LogbackType;

import java.util.Objects;

/**
 * @Description :  日志初始化管理器
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/2 11:16 AM
 */
public class LoggerContextManager {
    private static LogbackContext logbackContext;

    /**
     * 日志组件SDK初始化
     *
     * @param properties 日志属性配置
     */
    public static void init(LoggerProperties properties) {
        if (!properties.isEnabled()) {
            return;
        }
        if (Objects.nonNull(logbackContext)) {
            logbackContext.clear();
        }
        logbackContext = new LogbackContext(properties);
        // 初始化root logger
        LogbackAppender appender = new LogbackAppender();
        // appender name
        appender.setAppenderName(Logger.ROOT_LOGGER_NAME);
        // logger file path
        appender.setFilePath(properties.getRoot().getFilePath());
        // logger type
        appender.setLogbackType(LogbackType.ROOT);

        new LogbackRoot(properties).getLogger(Logger.ROOT_LOGGER_NAME, appender);
    }

    public static LogbackContext getLogbackContext() {
        if (Objects.isNull(logbackContext)) {
            throw new IllegalStateException("Log sdk not initialized");
        }
        return logbackContext;
    }
}
