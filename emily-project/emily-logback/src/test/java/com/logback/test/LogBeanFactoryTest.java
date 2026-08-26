package com.logback.test;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.read.ListAppender;
import com.emily.infrastructure.logback.factory.AppenderType;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import com.emily.infrastructure.logback.LogbackContextInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogBeanFactoryTest {
    private static final AppenderType<ListAppender<ILoggingEvent>> LIST_APPENDER =
            AppenderType.parameterized(ListAppender.class);

    private final LoggerContext context = new LoggerContext();

    @AfterEach
    void tearDown() {
        LogbackContextInitializer.shutdown();
        context.stop();
    }

    @Test
    void shouldIsolateLoggerAppenderAndComponentNamespaces() {
        String sharedName = "shared-name";
        org.slf4j.Logger logger = context.getLogger(sharedName);
        ListAppender<ILoggingEvent> appender = startedListAppender(sharedName);
        String component = "component";

        Assertions.assertSame(logger, LogBeanFactory.getOrCreateLogger(sharedName, () -> logger));
        LogBeanFactory.getOrCreateAppender(sharedName, LIST_APPENDER, () -> appender);
        LogBeanFactory.registerComponent(String.class, component);

        Assertions.assertSame(logger, LogBeanFactory.getOrCreateLogger(sharedName,
                () -> context.getLogger("unused")));
        Assertions.assertSame(appender,
                LogBeanFactory.getOrCreateAppender(sharedName, LIST_APPENDER, () -> startedListAppender("unused")));
        Assertions.assertSame(component, LogBeanFactory.getComponent(String.class));
    }

    @Test
    void shouldRejectAppenderTypeCollision() {
        String name = "type-collision";
        LogBeanFactory.getOrCreateAppender(name, LIST_APPENDER, () -> startedListAppender(name));

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> LogBeanFactory.getOrCreateAppender(name, AppenderType.ASYNC, AsyncAppender::new));

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
        ch.qos.logback.classic.Logger logger = context.getLogger(name);
        LogBeanFactory.getOrCreateAppender(name, LIST_APPENDER, () -> appender);
        LogBeanFactory.getOrCreateLogger(name, () -> logger);
        LogBeanFactory.registerComponent(String.class, "component");
        logger.addAppender(appender);

        LogBeanFactory.shutdownAndClear();

        Assertions.assertFalse(appender.isStarted());
        Assertions.assertFalse(logger.isAttached(appender));
        Assertions.assertTrue(LogBeanFactory.isEmpty());
        Assertions.assertTrue(LogBeanFactory.getAppenders(LIST_APPENDER).isEmpty());
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
