package com.emily.infrastructure.logback.factory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.core.Appender;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * 日志组件、Logger和Appender缓存容器。
 *
 * @author :  Emily
 * @since :  2024/1/1 9:47 AM
 */
public final class LogBeanFactory {
    private static final Map<Class<?>, Object> COMPONENT_MAP = new ConcurrentHashMap<>(32);
    private static final Map<String, Logger> LOGGER_MAP = new ConcurrentHashMap<>(32);
    private static final Map<String, Appender<ILoggingEvent>> APPENDER_MAP = new ConcurrentHashMap<>(64);
    private static final ReentrantReadWriteLock LIFECYCLE_LOCK = new ReentrantReadWriteLock();

    private LogBeanFactory() {
    }

    public static <T> void registerComponent(Class<T> type, T component) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(component, "component must not be null");
        LIFECYCLE_LOCK.readLock().lock();
        try {
            COMPONENT_MAP.putIfAbsent(type, component);
        } finally {
            LIFECYCLE_LOCK.readLock().unlock();
        }
    }

    public static <T> T getComponent(Class<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        Object component = COMPONENT_MAP.get(type);
        if (component == null) {
            throw new IllegalStateException("Component not found: " + type.getName());
        }
        return type.cast(component);
    }

    public static <T> List<T> getComponents(Class<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        return COMPONENT_MAP.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    public static Logger getLogger(String name) {
        Objects.requireNonNull(name, "name must not be null");
        return LOGGER_MAP.get(name);
    }

    public static void registerLogger(String name, Logger logger) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(logger, "logger must not be null");
        LIFECYCLE_LOCK.readLock().lock();
        try {
            LOGGER_MAP.putIfAbsent(name, logger);
        } finally {
            LIFECYCLE_LOCK.readLock().unlock();
        }
    }

    public static Logger getOrCreateLogger(String name, Supplier<Logger> factory) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        LIFECYCLE_LOCK.readLock().lock();
        try {
            return LOGGER_MAP.computeIfAbsent(name, key -> Objects.requireNonNull(factory.get(), "Logger factory returned null for " + name));
        } finally {
            LIFECYCLE_LOCK.readLock().unlock();
        }
    }

    public static <T extends Appender<ILoggingEvent>> T getOrCreateAppender(
            String name, AppenderType<T> type, Supplier<? extends T> factory) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        LIFECYCLE_LOCK.readLock().lock();
        try {
            Appender<ILoggingEvent> appender = APPENDER_MAP.computeIfAbsent(
                    name, key -> createAndValidateAppender(name, type, factory));
            if (!type.isInstance(appender)) {
                throw appenderTypeMismatch(name, type, appender);
            }
            return type.cast(appender);
        } finally {
            LIFECYCLE_LOCK.readLock().unlock();
        }
    }

    private static <T extends Appender<ILoggingEvent>> Appender<ILoggingEvent> createAndValidateAppender(
            String name, AppenderType<T> type, Supplier<? extends T> factory) {
        Appender<ILoggingEvent> created = Objects.requireNonNull(
                factory.get(), "Appender factory returned null for " + name);
        if (type.isInstance(created)) {
            return created;
        }
        if (created.isStarted()) {
            created.stop();
        }
        throw appenderTypeMismatch(name, type, created);
    }

    private static IllegalStateException appenderTypeMismatch(
            String name, AppenderType<?> type, Appender<ILoggingEvent> appender) {
        return new IllegalStateException("Appender " + name + " is " + appender.getClass().getName()
                + ", expected " + type.typeName());
    }

    public static <T extends Appender<ILoggingEvent>> List<T> getAppenders(AppenderType<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        return APPENDER_MAP.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    public static boolean isEmpty() {
        LIFECYCLE_LOCK.readLock().lock();
        try {
            return APPENDER_MAP.isEmpty() && LOGGER_MAP.isEmpty() && COMPONENT_MAP.isEmpty();
        } finally {
            LIFECYCLE_LOCK.readLock().unlock();
        }
    }

    /**
     * 停止所有已注册Appender并清空缓存。
     * AsyncAppender优先停止，以便在目标Appender关闭前刷新队列。
     */
    public static void shutdownAndClear() {
        LIFECYCLE_LOCK.writeLock().lock();
        try {
            RuntimeException failure = null;
            detachAppendersFromLoggers();
            failure = stopAppenders(true, failure);
            failure = stopAppenders(false, failure);
            APPENDER_MAP.clear();
            LOGGER_MAP.clear();
            COMPONENT_MAP.clear();
            if (failure != null) {
                throw failure;
            }
        } finally {
            LIFECYCLE_LOCK.writeLock().unlock();
        }
    }

    private static void detachAppendersFromLoggers() {
        for (Logger logger : LOGGER_MAP.values()) {
            if (logger instanceof ch.qos.logback.classic.Logger classicLogger) {
                for (Appender<ILoggingEvent> appender : APPENDER_MAP.values()) {
                    classicLogger.detachAppender(appender);
                }
            }
        }
    }

    private static RuntimeException stopAppenders(boolean async, RuntimeException failure) {
        for (Appender<ILoggingEvent> appender : APPENDER_MAP.values()) {
            if ((appender instanceof AsyncAppender) != async || !appender.isStarted()) {
                continue;
            }
            try {
                appender.stop();
            } catch (RuntimeException ex) {
                if (failure == null) {
                    failure = ex;
                } else {
                    failure.addSuppressed(ex);
                }
            }
        }
        return failure;
    }
}
