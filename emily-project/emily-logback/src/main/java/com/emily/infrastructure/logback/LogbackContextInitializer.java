package com.emily.infrastructure.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ClassicEnvUtil;
import com.emily.infrastructure.logback.configuration.context.LogbackContext;
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
    private static LogbackContext logbackContext;
    /**
     * 是否已经初始化，默认：false
     */
    private static boolean initialized;

    /**
     * 日志组件SDK初始化
     *
     * @param properties 日志属性配置
     */
    public static void initialize(LogbackProperties properties) {
        if (!properties.isEnabled()) {
            return;
        }
        if (initialized) {
            logbackContext.stopAndReset(LogHolder.LC);
        }
        // 初始化日志上下文
        List<LogbackContext> list = ClassicEnvUtil.loadFromServiceLoader(LogbackContext.class, LogbackContext.class.getClassLoader());
        if (list.isEmpty()) {
            System.out.println("Non existing log context");
            return;
        }
        logbackContext = list.getFirst();
        // 初始化
        logbackContext.initialize(LogHolder.LC, properties);
        // 启动上下文，初始化root logger对象
        logbackContext.start(properties);

        if (initialized) {
            LogHolder.LOG.warn("It has already been initialized,please do not repeatedly initialize the log sdk.");
        } else {
            LogHolder.LOG.info("Log sdk initialized");
        }
        // 设置为已初始化
        initialized = true;
    }

    public static LogbackContext getLogbackContext() {
        if (initialized) {
            return logbackContext;
        }
        throw new IllegalStateException("Log sdk not initialized");
    }


    public static class LogHolder {
        private static final LoggerContext LC = (LoggerContext) LoggerFactory.getILoggerFactory();
        private static final Logger LOG = LoggerFactory.getLogger(LogbackContextInitializer.class);
    }
}
