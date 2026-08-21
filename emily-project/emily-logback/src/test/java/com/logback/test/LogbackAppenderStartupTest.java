package com.logback.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.common.LogPathField;
import com.emily.infrastructure.logback.configuration.appender.LogbackConsoleAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logback.configuration.context.LogbackContext;
import com.emily.infrastructure.logback.configuration.type.LogbackType;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class LogbackAppenderStartupTest {

    private LoggerContext context;

    @BeforeEach
    void setUp() {
        context = new LoggerContext();
    }

    @AfterEach
    void tearDown() {
        LogBeanFactory.shutdownAndClear();
        context.stop();
    }

    @Test
    void shouldStartAndCacheRollingFileAppender(@TempDir Path tempDir) {
        LogbackProperties properties = initializedProperties(tempDir.toString());
        LogbackRollingFileAppender factory = LogBeanFactory.getComponent(LogbackRollingFileAppender.class);
        LogPathField field = groupField("startup-success");

        Appender<ILoggingEvent> appender = factory.getOrCreate(Level.INFO, field);

        Assertions.assertTrue(appender.isStarted());
        Assertions.assertSame(appender, factory.getOrCreate(Level.INFO, field));
    }

    @Test
    void shouldNotCacheRollingFileAppenderWhenStartupFails(@TempDir Path tempDir) {
        LogbackProperties properties = initializedProperties(tempDir.toString());
        properties.getAppender().getTimeRollingPolicy().setTotalSizeCap("invalid-size");
        LogbackRollingFileAppender factory = LogBeanFactory.getComponent(LogbackRollingFileAppender.class);
        LogPathField field = groupField("startup-failure");

        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.getOrCreate(Level.INFO, field));
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.getOrCreate(Level.INFO, field));
    }

    @Test
    void shouldStartConsoleAppender(@TempDir Path tempDir) {
        initializedProperties(tempDir.toString());
        LogbackConsoleAppender factory = LogBeanFactory.getComponent(LogbackConsoleAppender.class);

        Appender<ILoggingEvent> appender = factory.getOrCreate(Level.INFO);

        Assertions.assertTrue(appender.isStarted());
    }

    private LogbackProperties initializedProperties(String path) {
        LogbackProperties properties = new LogbackProperties();
        properties.getAppender().setPath(path);
        properties.getRoot().setConsole(false);
        new LogbackContext().initialize(context, properties);
        return properties;
    }

    private LogPathField groupField(String name) {
        return LogPathField.newBuilder()
                .withLoggerName(name)
                .withFilePath(name)
                .withLogbackType(LogbackType.GROUP)
                .build();
    }
}
