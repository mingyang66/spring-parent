package com.emily.infrastructure.logger.manager;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.configuration.context.LogbackContext;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description :  日志初始化管理器
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/2 11:16 AM
 */
public class LoggerContextInitializer {
    private static final LoggerContext LOGGER_CONTEXT = (LoggerContext) LoggerFactory.getILoggerFactory();
    private static final Logger logger = LoggerFactory.getLogger(LoggerContextInitializer.class);
    /**
     * logback sdk context
     */
    private static LogbackContext logbackContext;
    /**
     * 是否已经初始化，默认：false
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
        if (isAlreadyInitialized()) {
            logbackContext.stopAndReset();
        }
        // 初始化日志上下文
        logbackContext = new LogbackContext(properties, LOGGER_CONTEXT);
        // 初始化root logger对象
        logbackContext.start();
        if (isAlreadyInitialized()) {
            logger.warn("It has already been initialized,please do not repeatedly initialize the log sdk.");
        } else {
            logger.info("Log sdk initialized");
        }
        // 设置为已初始化
        markAsInitialized();
    }

    public static LogbackContext getLogbackContext() {
        if (isAlreadyInitialized()) {
            return logbackContext;
        }
        throw new IllegalStateException("Log sdk not initialized");
    }

    /**
     * 是否已经初始化过
     *
     * @return true-是 false-否
     */
    private static boolean isAlreadyInitialized() {
        return initialized;
    }

    /**
     * 标记为已经初始化
     */
    private static void markAsInitialized() {
        initialized = true;
    }

}
