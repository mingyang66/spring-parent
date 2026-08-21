package com.emily.infrastructure.logback;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logback.common.ClassicEnvUtil;
import com.emily.infrastructure.logback.configuration.context.LogbackContext;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import com.emily.infrastructure.logback.factory.LogbackPropertiesValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 日志初始化管理器
 * <a href="https://www.baeldung.com/logback">logback说明文档</a>
 *
 * @author Emily
 * @since :  Created in 2023/7/2 11:16 AM
 */
public class LogbackContextInitializer {
    /**
     * logback sdk context
     */
    private static volatile LogbackContext logbackContext;
    /**
     * 日志组件SDK初始化
     *
     * @param properties 日志属性配置
     */
    public static synchronized void initialize(LogbackProperties properties) {
        LogbackPropertiesValidator.validate(properties);
        if (!properties.isEnabled()) {
            return;
        }
        if (logbackContext != null && !LogBeanFactory.isEmpty()) {
            return;
        }
        // 初始化日志上下文
        List<LogbackContext> list = ClassicEnvUtil.loadFromServiceLoader(LogbackContext.class, LogbackContext.class.getClassLoader());
        if (list.isEmpty()) {
            throw new IllegalStateException("No LogbackContext implementation found");
        }
        LogbackContext candidate = list.getFirst();
        // 初始化
        candidate.initialize(LogHolder.LC, properties);
        // 初始化完成后发布上下文和状态
        logbackContext = candidate;
        LogHolder.LOG.info("Log sdk initialized");
    }

    public static LogbackContext getLogbackContext() {
        LogbackContext context = logbackContext;
        if (context != null && !LogBeanFactory.isEmpty()) {
            return context;
        }
        throw new IllegalStateException("Log sdk not initialized");
    }

    /**
     * 关闭日志SDK创建的资源并重置初始化状态，允许后续重新初始化。
     */
    public static synchronized void shutdown() {
        try {
            LogBeanFactory.shutdownAndClear();
        } finally {
            logbackContext = null;
        }
    }


    public static class LogHolder {
        private static final LoggerContext LC = (LoggerContext) LoggerFactory.getILoggerFactory();
        private static final Logger LOG = LoggerFactory.getLogger(LogbackContextInitializer.class);
    }
}
