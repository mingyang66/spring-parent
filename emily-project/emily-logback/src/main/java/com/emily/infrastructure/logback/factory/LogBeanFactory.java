package com.emily.infrastructure.logback.factory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
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
    // Keeps resource creation and shutdown mutually exclusive.
    private static final ReentrantReadWriteLock LIFECYCLE_LOCK = new ReentrantReadWriteLock();

    private LogBeanFactory() {
    }

    public static <T> void registerComponent(Class<T> type, T component) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(component, "component must not be null");
        LIFECYCLE_LOCK.readLock().lock();
        try {
            Object existing = COMPONENT_MAP.putIfAbsent(type, component);
            if (existing != null && existing != component) {
                throw new IllegalStateException("Component already registered: " + type.getName());
            }
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

    /**
     * Returns the cached Logger or creates one while holding the lifecycle read lock.
     * {@link #clear()} waits for an in-progress factory invocation before clearing caches.
     *
     * @param name    logger name
     * @param factory creates the Logger when absent
     * @return cached or newly created Logger
     */
    public static Logger getOrCreateLogger(String name, Supplier<? extends Logger> factory) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        LIFECYCLE_LOCK.readLock().lock();
        try {
            return LOGGER_MAP.computeIfAbsent(name, key -> Objects.requireNonNull(factory.get(), "Logger factory returned null for " + name));
        } finally {
            LIFECYCLE_LOCK.readLock().unlock();
        }
    }

    /**
     * Returns the cached Appender or creates one using its cache key while holding the lifecycle read lock.
     * {@link #clear()} waits for an in-progress mapping function before stopping resources.
     *
     * @param name            appender name
     * @param mappingFunction creates the Appender when absent
     * @param <T>             expected Appender type
     * @return cached or newly created Appender
     */
    public static <T extends Appender<ILoggingEvent>> T getOrCreateAppender(
            String name, Function<String, Appender<ILoggingEvent>> mappingFunction) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(mappingFunction, "mappingFunction must not be null");
        LIFECYCLE_LOCK.readLock().lock();
        try {
            Appender<ILoggingEvent> appender = APPENDER_MAP.computeIfAbsent(name, key -> Objects.requireNonNull(
                    mappingFunction.apply(key), "Appender mapping function must not return null for " + key));
            return castAppender(appender);
        } finally {
            LIFECYCLE_LOCK.readLock().unlock();
        }
    }

    public static <T extends Appender<ILoggingEvent>> List<T> getAppenders(Class<?> type) {
        Objects.requireNonNull(type, "type must not be null");
        return APPENDER_MAP.values().stream()
                .filter(type::isInstance)
                .map(LogBeanFactory::<T>castAppender)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Appender<ILoggingEvent>> T castAppender(Appender<ILoggingEvent> appender) {
        return (T) appender;
    }


    /**
     * 停止所有已注册Appender并清空缓存。
     * AsyncAppender优先停止，以便在目标Appender关闭前刷新队列。
     * This method acquires the lifecycle write lock and waits for in-progress resource creation.
     */
    public static void clear() {
        LIFECYCLE_LOCK.writeLock().lock();
        try {
            APPENDER_MAP.clear();
            LOGGER_MAP.clear();
            COMPONENT_MAP.clear();
        } finally {
            LIFECYCLE_LOCK.writeLock().unlock();
        }
    }
}
