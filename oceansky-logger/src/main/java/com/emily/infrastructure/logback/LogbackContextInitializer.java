package com.emily.infrastructure.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ClassicEnvUtil;
import com.emily.infrastructure.logback.configuration.context.Context;
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
    private static final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    private static final Logger logger = LoggerFactory.getLogger(LogbackContextInitializer.class);
    /**
     * logback sdk context
     */
    private static Context context;
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
        List<Context> list = ClassicEnvUtil.loadFromServiceLoader(Context.class, Context.class.getClassLoader());
        if (list.isEmpty()) {
            System.out.println("Non existing log context");
            return;
        }
        //context = EnvUtil.loadFromServiceLoader(Context.class); // new version expire
        context = list.get(0);
        // 对属性进行设置
        context.configure(properties, lc);
        // 初始化root logger对象
        context.start();
        if (isAlreadyInitialized()) {
            logger.warn("It has already been initialized,please do not repeatedly initialize the log sdk.");
        } else {
            logger.info("Log sdk initialized");
        }
        // 设置为已初始化
        markAsInitialized();
    }

    public static Context getContext() {
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

}
