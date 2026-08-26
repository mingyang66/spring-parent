package com.emily.infrastructure.logback;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logback.common.ClassicEnvUtil;
import com.emily.infrastructure.logback.configuration.context.LogbackContext;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * 日志初始化管理器
 * <a href="https://www.baeldung.com/logback">logback说明文档</a>
 *
 * @author Emily
 * @since :  Created in 2023/7/2 11:16 AM
 */
public final class LogbackContextInitializer {

    private static volatile LogbackContext logbackContext;

    private LogbackContextInitializer() {
    }

    /**
     * 日志组件SDK初始化
     *
     * @param properties 日志属性配置
     */
    public static synchronized void initialize(LogbackProperties properties) {
        Objects.requireNonNull(properties, "properties must not be null");
        if (!properties.isEnabled()) {
            return;
        }
        if (logbackContext != null) {
            return;
        }
        List<LogbackContext> list = ClassicEnvUtil.loadFromServiceLoader(LogbackContext.class, LogbackContext.class.getClassLoader());
        if (list.isEmpty()) {
            throw new IllegalStateException("No LogbackContext implementation found");
        }
        LogbackContext candidate = list.getFirst();
        candidate.initialize(LogHolder.LC, properties);
        logbackContext = candidate;
        LogHolder.LOG.info("Log sdk initialized");
    }

    public static LogbackContext getLogbackContext() {
        LogbackContext context = logbackContext;
        if (context == null) {
            throw new IllegalStateException("Log sdk not initialized");
        }
        return context;
    }

    /**
     * 关闭日志SDK创建的资源并重置初始化状态，允许后续重新初始化。
     */
    public static synchronized void shutdown() {
        LogbackContext context = logbackContext;
        try {
            if (context != null) {
                context.shutdown();
            } else {
                LogBeanFactory.shutdownAndClear();
            }
        } finally {
            logbackContext = null;
        }
    }

    private static class LogHolder {
        private static final LoggerContext LC = (LoggerContext) LoggerFactory.getILoggerFactory();
        private static final Logger LOG = LoggerFactory.getLogger(LogbackContextInitializer.class);
    }
}
