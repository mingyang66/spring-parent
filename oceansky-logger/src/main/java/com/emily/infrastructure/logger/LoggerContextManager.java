package com.emily.infrastructure.logger;

import com.emily.infrastructure.logger.configuration.context.LogbackContext;

import java.util.Objects;

/**
 * @Description :  日志初始化管理器
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/2 11:16 AM
 */
public class LoggerContextManager {
    /**
     * 日志组件SDK初始化
     *
     * @param properties 日志属性配置
     */
    public static void init(LoggerProperties properties) {
        if (!properties.isEnabled()) {
            return;
        }
        if (Objects.nonNull(LoggerFactory.CONTEXT)) {
            LoggerFactory.CONTEXT.clear();
        }
        LoggerFactory.CONTEXT = new LogbackContext(properties);
    }
}
