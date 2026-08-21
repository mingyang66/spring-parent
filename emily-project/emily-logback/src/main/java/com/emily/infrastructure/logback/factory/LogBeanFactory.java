package com.emily.infrastructure.logback.factory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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

    private LogBeanFactory() {
    }

    public static <T> void registerComponent(Class<T> type, T component) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(component, "component must not be null");
        COMPONENT_MAP.putIfAbsent(type, component);
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
        LOGGER_MAP.putIfAbsent(name, logger);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Appender<ILoggingEvent>> T getOrCreateAppender(
            String name, Class<?> type, Supplier<? extends T> factory) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        Appender<ILoggingEvent> appender = APPENDER_MAP.computeIfAbsent(name, key -> factory.get());
        if (!type.isInstance(appender)) {
            throw new IllegalStateException("Appender " + name + " is " + appender.getClass().getName()
                    + ", expected " + type.getName());
        }
        return (T) appender;
    }

    public static <T extends Appender<ILoggingEvent>> List<T> getAppenders(Class<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        return APPENDER_MAP.values().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    public static void clear() {
        APPENDER_MAP.clear();
        LOGGER_MAP.clear();
        COMPONENT_MAP.clear();
    }
}
