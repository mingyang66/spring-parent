package com.emily.infrastructure.logback.test;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.read.ListAppender;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import com.emily.infrastructure.logback.LogbackContextInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogBeanFactoryTest {
    private final LoggerContext context = new LoggerContext();

    @AfterEach
    void tearDown() {
        LogBeanFactory.shutdownAndClear();
        LogbackContextInitializer.stopAndReset();
    }

    @Test
    void shouldIsolateLoggerAppenderAndComponentNamespaces() {
        String sharedName = "shared-name";
        Logger logger = context.getLogger(sharedName);
        ListAppender<ILoggingEvent> appender = startedListAppender(sharedName);
        String component = "component";

        Assertions.assertSame(logger, LogBeanFactory.getOrCreateLogger(sharedName, () -> logger));
        LogBeanFactory.getOrCreateAppender(sharedName, name -> appender);
        LogBeanFactory.registerComponent(String.class, component);

        Assertions.assertSame(logger, LogBeanFactory.getOrCreateLogger(sharedName,
                () -> context.getLogger("unused")));
        Assertions.assertSame(appender,
                LogBeanFactory.getOrCreateAppender(sharedName, name -> startedListAppender("unused")));
        Assertions.assertSame(component, LogBeanFactory.getComponent(String.class));
    }

    @Test
    void shouldFailWhenComponentIsMissing() {
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> LogBeanFactory.getComponent(ConsoleAppender.class));
        Assertions.assertTrue(exception.getMessage().contains(ConsoleAppender.class.getName()));
    }

    @Test
    void shouldRejectDuplicateComponentRegistration() {
        LogBeanFactory.registerComponent(String.class, "first");

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> LogBeanFactory.registerComponent(String.class, "second"));

        Assertions.assertTrue(exception.getMessage().contains(String.class.getName()));
        Assertions.assertEquals("first", LogBeanFactory.getComponent(String.class));
    }

    @Test
    void shouldStopResourcesBeforeClearingCaches() {
        String name = "shutdown";
        ListAppender<ILoggingEvent> appender = startedListAppender(name);
        ch.qos.logback.classic.Logger logger = context.getLogger(name);
        LogBeanFactory.getOrCreateAppender(name, key -> appender);
        LogBeanFactory.getOrCreateLogger(name, () -> logger);
        LogBeanFactory.registerComponent(String.class, "component");
        logger.addAppender(appender);

        LogBeanFactory.shutdownAndClear();

        Assertions.assertFalse(appender.isStarted());
        Assertions.assertFalse(logger.isAttached(appender));
        Assertions.assertTrue(LogBeanFactory.<ListAppender<ILoggingEvent>>getAppenders(ListAppender.class).isEmpty());
        Assertions.assertThrows(IllegalStateException.class, () -> LogBeanFactory.getComponent(String.class));
    }

    private ListAppender<ILoggingEvent> startedListAppender(String name) {
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setContext(context);
        appender.setName(name);
        appender.start();
        return appender;
    }
}
