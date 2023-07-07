package com.emily.infrastructure.logger;

import com.emily.infrastructure.logger.configuration.context.LogbackContext;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;

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
    }

    public static LogbackContext getLogbackContext() {
        if (Objects.isNull(logbackContext)) {
            throw new IllegalStateException("日志SDK未初始化");
        }
        return logbackContext;
    }
}
