package com.emily.infrastructure.logback.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.MarkerFilter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.NopStatusListener;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.LogbackContextInitializer;
import com.emily.infrastructure.logback.common.LogNameUtils;
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
import java.nio.file.Files;
import java.util.List;

public class LogbackAppenderStartupTest {

    private LoggerContext context;

    @BeforeEach
    void setUp() {
        context = new LoggerContext();
    }

    @AfterEach
    void tearDown() {
        LogbackContextInitializer.shutdown();
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

    @Test
    void shouldRestoreLoggerContextStateWhenInitializationFails(@TempDir Path tempDir) throws Exception {
        Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);
        root.setAdditive(true);
        context.setPackagingDataEnabled(false);
        ListAppender<ILoggingEvent> originalAppender = new ListAppender<>();
        originalAppender.setContext(context);
        originalAppender.setName("original");
        originalAppender.start();
        root.addAppender(originalAppender);
        MarkerFilter originalFilter = markerFilter("original-filter");
        context.addTurboFilter(originalFilter);
        NopStatusListener originalStatusListener = new NopStatusListener();
        context.getStatusManager().add(originalStatusListener);
        List<?> originalStatusListeners = context.getStatusManager().getCopyOfStatusListenerList();

        Path regularFile = Files.createFile(tempDir.resolve("not-a-directory"));
        LogbackProperties properties = new LogbackProperties();
        properties.setPackagingData(true);
        properties.getAppender().setPath(regularFile.toString());
        properties.getMarker().getAcceptMarker().add("new-filter");

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> new LogbackContext().initialize(context, properties));

        Assertions.assertEquals(Level.ERROR, root.getLevel());
        Assertions.assertTrue(root.isAdditive());
        Assertions.assertFalse(context.isPackagingDataEnabled());
        Assertions.assertTrue(root.isAttached(originalAppender));
        Assertions.assertTrue(originalAppender.isStarted());
        Assertions.assertEquals(List.of(originalFilter), context.getTurboFilterList());
        Assertions.assertEquals(originalStatusListeners, context.getStatusManager().getCopyOfStatusListenerList());
        Assertions.assertTrue(LogBeanFactory.isEmpty());
    }

    @Test
    void shouldRestoreLoggerContextStateAfterSuccessfulShutdown(@TempDir Path tempDir) {
        Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);
        root.setAdditive(true);
        context.setPackagingDataEnabled(false);
        ListAppender<ILoggingEvent> originalRootAppender = new ListAppender<>();
        originalRootAppender.setContext(context);
        originalRootAppender.setName("CONSOLE");
        originalRootAppender.start();
        root.addAppender(originalRootAppender);
        MarkerFilter originalFilter = markerFilter("original-filter");
        context.addTurboFilter(originalFilter);

        String groupLoggerName = LogNameUtils.joinLogName(
                LogbackType.GROUP, "runtime", null, LogbackAppenderStartupTest.class);
        Logger groupLogger = context.getLogger(groupLoggerName);
        groupLogger.setLevel(Level.ERROR);
        groupLogger.setAdditive(true);
        ListAppender<ILoggingEvent> originalGroupAppender = new ListAppender<>();
        originalGroupAppender.setContext(context);
        originalGroupAppender.setName("group-original");
        originalGroupAppender.start();
        groupLogger.addAppender(originalGroupAppender);

        LogbackProperties properties = new LogbackProperties();
        properties.getAppender().setPath(tempDir.toString());
        properties.setPackagingData(true);
        properties.getRoot().setLevel(org.slf4j.event.Level.INFO);
        properties.getRoot().setConsole(false);
        properties.getGroup().setLevel(org.slf4j.event.Level.INFO);
        properties.getMarker().getAcceptMarker().add("sdk-filter");
        LogbackContext logbackContext = new LogbackContext();
        logbackContext.initialize(context, properties);
        logbackContext.getLogger(LogbackAppenderStartupTest.class, LogbackType.GROUP, "runtime", null);

        logbackContext.shutdown();

        Assertions.assertEquals(Level.ERROR, root.getLevel());
        Assertions.assertTrue(root.isAdditive());
        Assertions.assertFalse(context.isPackagingDataEnabled());
        Assertions.assertTrue(root.isAttached(originalRootAppender));
        Assertions.assertTrue(originalRootAppender.isStarted());
        Assertions.assertEquals(Level.ERROR, groupLogger.getLevel());
        Assertions.assertTrue(groupLogger.isAdditive());
        Assertions.assertTrue(groupLogger.isAttached(originalGroupAppender));
        Assertions.assertTrue(originalGroupAppender.isStarted());
        Assertions.assertEquals(List.of(originalFilter), context.getTurboFilterList());
        Assertions.assertTrue(LogBeanFactory.isEmpty());
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

    private MarkerFilter markerFilter(String name) {
        MarkerFilter filter = new MarkerFilter();
        filter.setContext(context);
        filter.setName(name);
        filter.setMarker(name);
        filter.setOnMatch(FilterReply.ACCEPT.name());
        filter.setOnMismatch(FilterReply.NEUTRAL.name());
        filter.start();
        return filter;
    }
}
