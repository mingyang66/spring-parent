package com.emily.infrastructure.logback.utils;

import ch.qos.logback.classic.Logger;
import com.emily.infrastructure.logback.builder.LogbackBuilder;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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
    /**
     * 开启logback日志组件
     */
    private static LogbackBuilder builder;

    public static <T> Logger getLogger(String path, String fileName) {
        return builder.getLogger(path, fileName);
    }

    public static <T> void info(Class<T> clazz, String msg) {
        LoggerFactory.getLogger(clazz).info(msg);
    }

    public static <T> void warn(Class<T> clazz, String msg) {
        LoggerFactory.getLogger(clazz).warn(msg);
    }

    public static <T> void debug(Class<T> clazz, String msg) {
        LoggerFactory.getLogger(clazz).debug(msg);
    }

    public static <T> void error(Class<T> clazz, String msg) {
        LoggerFactory.getLogger(clazz).error(msg);
    }

    public static <T> void trace(Class<T> clazz, String msg) {
        LoggerFactory.getLogger(clazz).trace(msg);
    }

    public static <T> void module(Class<T> clazz, String path, String fileName, String msg) {
        if (Objects.nonNull(builder)) {
            builder.getLogger(path, fileName).info(msg);
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

    public static void setBuilder(LogbackBuilder builder) {
        LoggerUtils.builder = builder;
    }
}
