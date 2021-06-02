package com.emily.infrastructure.logback.utils;

import com.emily.infrastructure.logback.builder.LogbackBuilder;
import org.slf4j.LoggerFactory;

/**
 * @author Emily
 * @Description: 日志工具类 日志级别总共有TARCE < DEBUG < INFO < WARN < ERROR < FATAL，且级别是逐渐提供，
 * 如果日志级别设置为INFO，则意味TRACE和DEBUG级别的日志都看不到。
 * @Version: 1.0
 */
public class LoggerUtils {
    /**
     * 当前开发模式
     */
    private static boolean debug;
    private static boolean ENABLE;

    public static <T> void info(Class<T> clazz, String msg) {
        if (ENABLE) {
            LogbackBuilder.getLogger().info(msg);
        } else {
            LoggerFactory.getLogger(clazz).info(msg);
        }
    }

    public static <T> void warn(Class<T> clazz, String msg) {
        if (ENABLE) {
            LogbackBuilder.getLogger().warn(msg);
        } else {
            LoggerFactory.getLogger(clazz).warn(msg);
        }
    }

    public static <T> void debug(Class<T> clazz, String msg) {
        if (ENABLE) {
            LogbackBuilder.getLogger().debug(msg);
        } else {
            LoggerFactory.getLogger(clazz).debug(msg);
        }
    }

    public static <T> void error(Class<T> clazz, String msg) {
        if (ENABLE) {
            LogbackBuilder.getLogger().error(msg);
        } else {
            LoggerFactory.getLogger(clazz).error(msg);
        }
    }

    public static <T> void trace(Class<T> clazz, String msg) {
        if (ENABLE) {
            LogbackBuilder.getLogger().trace(msg);
        } else {
            LoggerFactory.getLogger(clazz).trace(msg);
        }
    }

    public static <T> void module(Class<T> clazz, String path, String fileName, String msg) {
        if (ENABLE) {
            LogbackBuilder.getLogger(path, fileName).info(msg);
        } else {
            LoggerFactory.getLogger(clazz).info(msg);
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean isDebug) {
        debug = isDebug;
    }

    public static void setBuilder(boolean enable) {
        LoggerUtils.ENABLE = enable;
    }
}
