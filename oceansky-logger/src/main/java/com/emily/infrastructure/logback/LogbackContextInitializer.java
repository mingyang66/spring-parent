package com.emily.infrastructure.logback;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logback.common.ClassicEnvUtil;
import com.emily.infrastructure.logback.configuration.spi.ContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 日志初始化管理器
 *
 * @author Emily
 * @since :  Created in 2023/7/2 11:16 AM
 */
public class LogbackContextInitializer {
    /**
     * logback sdk context
     */
    private static ContextProvider context;
    /**
     * 是否已经初始化，默认：false
     */
    private static boolean initialized;

    /**
     * 日志组件SDK初始化
     *
     * @param properties 日志属性配置
     */
    public static void init(LogbackProperties properties) {
        if (!properties.isEnabled()) {
            return;
        }
        if (isAlreadyInitialized()) {
            context.stopAndReset();
        }
        // 初始化日志上下文
        List<ContextProvider> list = ClassicEnvUtil.loadFromServiceLoader(ContextProvider.class, ContextProvider.class.getClassLoader());
        if (list.isEmpty()) {
            System.out.println("Non existing log context");
            return;
        }
        context = list.get(0);
        // 初始化
        context.initialize(LogHolder.LC, properties);

        if (isAlreadyInitialized()) {
            LogHolder.LOG.warn("It has already been initialized,please do not repeatedly initialize the log sdk.");
        } else {
            LogHolder.LOG.info("Log sdk initialized");
        }
        // 设置为已初始化
        markAsInitialized();
    }

    public static ContextProvider getContextProvider() {
        if (isAlreadyInitialized()) {
            return context;
        }
        throw new IllegalStateException("Log sdk not initialized");
    }

    /**
     * 是否已经初始化过
     *
     * @return true-是 false-否
     */
    static boolean isAlreadyInitialized() {
        return initialized;
    }

    /**
     * 标记为已经初始化
     */
    static void markAsInitialized() {
        initialized = true;
    }

    public static class LogHolder {
        private static final LoggerContext LC = (LoggerContext) LoggerFactory.getILoggerFactory();
        private static final Logger LOG = LoggerFactory.getLogger(LogbackContextInitializer.class);
    }
}
