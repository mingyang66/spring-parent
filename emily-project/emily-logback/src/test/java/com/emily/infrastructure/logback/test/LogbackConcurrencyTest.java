package com.emily.infrastructure.logback.test;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.emily.infrastructure.logback.LogbackContextInitializer;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.configuration.context.LogbackContext;
import com.emily.infrastructure.logback.configuration.filter.LogLevelFilter;
import com.emily.infrastructure.logback.configuration.type.LogbackType;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LogbackConcurrencyTest {

    private final List<LoggerContext> contexts = new ArrayList<>();

    @AfterEach
    void tearDown() {
        LogbackContextInitializer.shutdown();
        contexts.forEach(LoggerContext::stop);
    }

    @Test
    void shouldRegisterSameAsyncAppenderOnceConcurrently() throws Exception {
        LoggerContext context = newContext();
        ListAppender<ILoggingEvent> target = startedTarget(context);
        LogBeanFactory.registerComponent(LogLevelFilter.class, new LogLevelFilter(context));
        LogbackAsyncAppender factory = new LogbackAsyncAppender(context, new LogbackProperties());
        int taskCount = 32;
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);

        try {
            List<Future<AsyncAppender>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    return factory.getOrCreate(target, Level.INFO);
                }));
            }
            start.countDown();

            Set<AsyncAppender> appenders = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
            for (Future<AsyncAppender> future : futures) {
                appenders.add(future.get());
            }

            Assertions.assertEquals(1, appenders.size());
            Assertions.assertTrue(appenders.iterator().next().isStarted());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldInitializeConcurrentlyWithoutFailure() throws Exception {
        LogbackContextInitializer.initialize(new LogbackProperties());
        LogbackContext expected = LogbackContextInitializer.getLogbackContext();
        int taskCount = 8;
        ExecutorService executor = Executors.newFixedThreadPool(taskCount);
        CountDownLatch start = new CountDownLatch(1);

        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    LogbackContextInitializer.initialize(new LogbackProperties());
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> future : futures) {
                future.get();
            }
            Assertions.assertSame(expected, LogbackContextInitializer.getLogbackContext());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldCreateSameLoggerOnceConcurrently() throws Exception {
        LoggerContext context = newContext();
        LogbackProperties properties = new LogbackProperties();
        properties.getRoot().setConsole(false);
        properties.getGroup().setConsole(false);
        LogbackContext logbackContext = new LogbackContext();
        logbackContext.initialize(context, properties);
        int taskCount = 32;
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);

        try {
            List<Future<Logger>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    return logbackContext.getLogger(LogbackConcurrencyTest.class, LogbackType.GROUP, "concurrency", null);
                }));
            }
            start.countDown();

            Set<Logger> loggers = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
            for (Future<Logger> future : futures) {
                loggers.add(future.get());
            }
            Assertions.assertEquals(1, loggers.size());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldRejectRepeatedInitializationOnSameInstance() {
        LoggerContext context = newContext();
        LogbackContext logbackContext = new LogbackContext();
        LogbackProperties properties = new LogbackProperties();
        properties.getRoot().setConsole(false);

        logbackContext.initialize(context, properties);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> logbackContext.initialize(context, properties));
        Assertions.assertTrue(exception.getMessage().contains("already been initialized"));
    }

    @Test
    void shouldRejectInitializationWhenGlobalContainerIsInUse() {
        LoggerContext firstContext = newContext();
        LogbackProperties properties = new LogbackProperties();
        properties.getRoot().setConsole(false);
        new LogbackContext().initialize(firstContext, properties);

        LoggerContext secondContext = newContext();
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> new LogbackContext().initialize(secondContext, properties));
        Assertions.assertTrue(exception.getMessage().contains("container is not empty"));
    }

    @Test
    void shouldReinitializeAfterShutdown() {
        LogbackContextInitializer.initialize(new LogbackProperties());
        LogbackContext first = LogbackContextInitializer.getLogbackContext();

        LogbackContextInitializer.shutdown();
        Assertions.assertThrows(IllegalStateException.class, LogbackContextInitializer::getLogbackContext);

        LogbackContextInitializer.initialize(new LogbackProperties());
        LogbackContext second = LogbackContextInitializer.getLogbackContext();

        Assertions.assertNotSame(first, second);
    }

    private LoggerContext newContext() {
        LoggerContext context = new LoggerContext();
        contexts.add(context);
        return context;
    }

    private ListAppender<ILoggingEvent> startedTarget(LoggerContext context) {
        ListAppender<ILoggingEvent> target = new ListAppender<>();
        target.setContext(context);
        target.setName("target-" + UUID.randomUUID());
        target.start();
        return target;
    }
}
