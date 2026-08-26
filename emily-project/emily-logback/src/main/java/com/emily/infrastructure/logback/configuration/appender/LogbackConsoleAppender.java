package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.encoder.LogbackConsoleLayoutEncoder;
import com.emily.infrastructure.logback.configuration.filter.LogThresholdLevelFilter;
import com.emily.infrastructure.logback.factory.LogBeanFactory;

import java.util.Objects;

/**
 * 控制台Appender工厂，负责创建和注册控制台输出Appender。
 *
 * @author Emily
 * @since 2020/08/04
 */
public class LogbackConsoleAppender {

    public static final String CONSOLE = "console";

    private final LoggerContext context;
    private final LogbackProperties properties;

    public LogbackConsoleAppender(LoggerContext context, LogbackProperties properties) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    public Appender<ILoggingEvent> getOrCreate(Level level) {
        return LogBeanFactory.getOrCreateAppender(CONSOLE,
                name -> createAppender(level));
    }

    private ConsoleAppender<ILoggingEvent> createAppender(Level level) {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setContext(context);
        appender.setName(CONSOLE);
        appender.addFilter(LogBeanFactory.getComponent(LogThresholdLevelFilter.class).getFilter(level));
        appender.setEncoder(LogBeanFactory.getComponent(LogbackConsoleLayoutEncoder.class).getEncoder(getFilePattern()));
        appender.setImmediateFlush(true);
        appender.setWithJansi(properties.getRoot().isWithJansi());
        appender.start();
        if (!appender.isStarted()) {
            throw new IllegalStateException("Failed to start console appender " + CONSOLE);
        }
        return appender;
    }

    private String getFilePattern() {
        return properties.getRoot().getConsolePattern();
    }
}
