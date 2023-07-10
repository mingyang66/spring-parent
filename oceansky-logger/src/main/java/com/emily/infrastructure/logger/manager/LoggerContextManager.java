package com.emily.infrastructure.logger.manager;

import ch.qos.logback.classic.Logger;
import com.emily.infrastructure.logger.configuration.context.LogbackContext;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;

/**
 * @Description :  日志初始化管理器
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/2 11:16 AM
 */
public class LoggerContextManager {
    private static LogbackContext logbackContext;
    private static org.slf4j.Logger logger;
    /**
     * 是否已经初始化
     */
    private static boolean initialized = false;

    /**
     * 日志组件SDK初始化
     *
     * @param properties 日志属性配置
     */
    public static void init(LoggerProperties properties) {
        if (!properties.isEnabled()) {
            return;
        }
        if (initialized) {
            logbackContext.reset();
            logger.warn("It has already been initialized,please do not repeatedly initialize the log sdk.");
        }
        // 初始化日志上下文
        logbackContext = new LogbackContext(properties);
        // 修改root logger
        logger = logbackContext.getRootLogger(Logger.ROOT_LOGGER_NAME);

        if (!initialized) {
            logger.info("Log sdk initialized");
        }
        // 设置为已初始化
        initialized = true;
    }

    public static LogbackContext getLogbackContext() {
        if (!initialized) {
            throw new IllegalStateException("Log sdk not initialized");
        }
        return logbackContext;
    }
}
