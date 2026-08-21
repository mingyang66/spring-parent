package com.emily.infrastructure.logback.factory;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;

import java.util.Objects;
import java.util.function.Function;

/**
 * 保留Appender运行时原始类型和编译期事件类型的类型令牌。
 */
public final class AppenderType<T extends Appender<ILoggingEvent>> {
    public static final AppenderType<AsyncAppender> ASYNC = of(AsyncAppender.class);
    public static final AppenderType<ConsoleAppender<ILoggingEvent>> CONSOLE = parameterized(ConsoleAppender.class);
    public static final AppenderType<RollingFileAppender<ILoggingEvent>> ROLLING_FILE = parameterized(RollingFileAppender.class);

    private final Class<?> rawType;
    private final Function<Appender<ILoggingEvent>, T> caster;

    private AppenderType(Class<?> rawType, Function<Appender<ILoggingEvent>, T> caster) {
        this.rawType = rawType;
        this.caster = caster;
    }

    public static <T extends Appender<ILoggingEvent>> AppenderType<T> of(Class<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        return new AppenderType<>(type, type::cast);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Appender<ILoggingEvent>> AppenderType<T> parameterized(Class<?> rawType) {
        Objects.requireNonNull(rawType, "rawType must not be null");
        if (!Appender.class.isAssignableFrom(rawType)) {
            throw new IllegalArgumentException("Not an Appender type: " + rawType.getName());
        }
        return new AppenderType<>(rawType, appender -> (T) appender);
    }

    boolean isInstance(Appender<ILoggingEvent> appender) {
        return rawType.isInstance(appender);
    }

    T cast(Appender<ILoggingEvent> appender) {
        return caster.apply(appender);
    }

    String typeName() {
        return rawType.getName();
    }
}
