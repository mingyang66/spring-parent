package com.logback.test;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.emily.infrastructure.logback.LogbackContextInitializer;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        contexts.forEach(LoggerContext::stop);
        LogBeanFactory.clear();
    }

    @Test
    void shouldRegisterSameAsyncAppenderOnceConcurrently() throws Exception {
        LoggerContext context = newContext();
        ListAppender<ILoggingEvent> target = startedTarget(context);
        LogbackAsyncAppender factory = new LogbackAsyncAppender(context, new LogbackProperties());
        int taskCount = 32;
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);

        try {
            List<Future<AsyncAppender>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    return factory.getOrCreate(target);
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
            Assertions.assertNotNull(LogbackContextInitializer.getLogbackContext());
        } finally {
            executor.shutdownNow();
        }
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
