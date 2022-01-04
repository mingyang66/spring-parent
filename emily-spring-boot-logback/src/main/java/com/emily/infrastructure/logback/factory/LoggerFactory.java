package com.emily.infrastructure.logback.factory;

import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.context.LogbackContext;
import com.emily.infrastructure.logback.configuration.enumeration.LogbackType;
import com.emily.infrastructure.logback.exception.UninitializedException;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * @author Emily
 * @Description: 日志工具类 日志级别总共有TARCE < DEBUG < INFO < WARN < ERROR < FATAL，且级别是逐渐提供，
 * 如果日志级别设置为INFO，则意味TRACE和DEBUG级别的日志都看不到。
 * @Version: 1.0
 */
public class LoggerFactory {
    /**
     * 加锁对象
     */
    private static Object lock = new Object();
    /**
     * Logger日志上下文
     */
    public static LogbackContext CONTEXT;
    /**
     * 容器上下文
     */
    public static ApplicationContext applicationContext;

    public static <T> Logger getLogger(Class<T> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }

    public static <T> Logger getGroupLogger(Class<T> clazz, String path) {
        validLoggerContext();
        return CONTEXT.getLogger(clazz, path, null, LogbackType.GROUP);
    }

    public static <T> Logger getGroupLogger(Class<T> clazz, String path, String fileName) {
        validLoggerContext();
        return CONTEXT.getLogger(clazz, path, fileName, LogbackType.GROUP);
    }


    public static <T> Logger getModuleLogger(Class<T> clazz, String modulePath, String fileName) {
        validLoggerContext();
        return CONTEXT.getLogger(clazz, modulePath, fileName, LogbackType.MODULE);
    }

    /**
     * 校验Logger上下文的有效性
     */
    private static void validLoggerContext() {
        if (CONTEXT == null) {
            synchronized (lock) {
                String[] beanNamesForType = applicationContext.getBeanNamesForType(LogbackProperties.class);
                if (beanNamesForType.length > 0) {
                    CONTEXT = new LogbackContext(applicationContext.getBean(LogbackProperties.class));
                } else {
                    CONTEXT = new LogbackContext(new LogbackProperties());
                }
            }
        }
        if (CONTEXT == null) {
            throw new UninitializedException();
        }
    }
}
