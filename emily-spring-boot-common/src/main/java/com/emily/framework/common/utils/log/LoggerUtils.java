package com.emily.framework.common.utils.log;

import com.emily.framework.common.utils.log.accesslog.builder.AccessLogBuilder;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @Description: 日志工具类 日志级别总共有TARCE < DEBUG < INFO < WARN < ERROR < FATAL，且级别是逐渐提供，
 * 如果日志级别设置为INFO，则意味TRACE和DEBUG级别的日志都看不到。
 * @Version: 1.0
 */
public class LoggerUtils {
    /**
     * 当前开发模式
     */
    private static boolean debug = false;
    private static AccessLogBuilder builder = null;

    public static <T> void info(Class<T> clazz, String msg) {
        if (Objects.nonNull(builder)) {
            builder.getLogger(clazz).info(msg);
        } else {
            LoggerFactory.getLogger(clazz).info(msg);
        }
    }

    public static <T> void warn(Class<T> clazz, String msg) {
        if (Objects.nonNull(builder)) {
            builder.getLogger(clazz).warn(msg);
        } else {
            LoggerFactory.getLogger(clazz).warn(msg);
        }
    }

    public static <T> void debug(Class<T> clazz, String msg) {
        if (Objects.nonNull(builder)) {
            builder.getLogger(clazz).debug(msg);
        } else {
            LoggerFactory.getLogger(clazz).debug(msg);
        }
    }

    public static <T> void error(Class<T> clazz, String msg) {
        if (Objects.nonNull(builder)) {
            builder.getLogger(clazz).error(msg);
        } else {
            LoggerFactory.getLogger(clazz).error(msg);
        }
    }

    public static <T> void trace(Class<T> clazz, String msg) {
        if (Objects.nonNull(builder)) {
            builder.getLogger(clazz).trace(msg);
        } else {
            LoggerFactory.getLogger(clazz).trace(msg);
        }
    }

    public static <T> void module(Class<T> clazz, String moduleName, String msg) {
        if (Objects.nonNull(builder)) {
            builder.getLogger(clazz, moduleName).info(msg);
        } else {
            LoggerFactory.getLogger(moduleName).info(msg);
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean isDebug) {
        debug = isDebug;
    }

    public static AccessLogBuilder getBuilder() {
        return builder;
    }

    public static void setBuilder(AccessLogBuilder builder) {
        LoggerUtils.builder = builder;
    }
}
