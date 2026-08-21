package com.logback.test;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.read.ListAppender;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogBeanFactoryTest {

    private final LoggerContext context = new LoggerContext();

    @AfterEach
    void tearDown() {
        LogBeanFactory.shutdownAndClear();
        context.stop();
    }

    @Test
    void shouldIsolateLoggerAppenderAndComponentNamespaces() {
        String sharedName = "shared-name";
        org.slf4j.Logger logger = context.getLogger(sharedName);
        ListAppender<ILoggingEvent> appender = startedListAppender(sharedName);
        String component = "component";

        LogBeanFactory.registerLogger(sharedName, logger);
        LogBeanFactory.getOrCreateAppender(sharedName, ListAppender.class, () -> appender);
        LogBeanFactory.registerComponent(String.class, component);

        Assertions.assertSame(logger, LogBeanFactory.getLogger(sharedName));
        Assertions.assertSame(appender,
                LogBeanFactory.getOrCreateAppender(sharedName, ListAppender.class, () -> startedListAppender("unused")));
        Assertions.assertSame(component, LogBeanFactory.getComponent(String.class));
    }

    @Test
    void shouldRejectAppenderTypeCollision() {
        String name = "type-collision";
        LogBeanFactory.getOrCreateAppender(name, ListAppender.class, () -> startedListAppender(name));

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> LogBeanFactory.getOrCreateAppender(name, AsyncAppender.class, AsyncAppender::new));

        Assertions.assertTrue(exception.getMessage().contains(AsyncAppender.class.getName()));
    }

    @Test
    void shouldFailWhenComponentIsMissing() {
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> LogBeanFactory.getComponent(ConsoleAppender.class));
        Assertions.assertTrue(exception.getMessage().contains(ConsoleAppender.class.getName()));
    }

    @Test
    void shouldStopResourcesBeforeClearingCaches() {
        String name = "shutdown";
        ListAppender<ILoggingEvent> appender = startedListAppender(name);
        org.slf4j.Logger logger = context.getLogger(name);
        LogBeanFactory.getOrCreateAppender(name, ListAppender.class, () -> appender);
        LogBeanFactory.registerLogger(name, logger);
        LogBeanFactory.registerComponent(String.class, "component");

        LogBeanFactory.shutdownAndClear();

        Assertions.assertFalse(appender.isStarted());
        Assertions.assertNull(LogBeanFactory.getLogger(name));
        Assertions.assertTrue(LogBeanFactory.getAppenders(ListAppender.class).isEmpty());
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
