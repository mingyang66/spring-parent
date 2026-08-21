package com.logback.test;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.LogbackContextInitializer;
import com.emily.infrastructure.logback.configuration.appender.AsyncAppenderQueueSnapshot;
import com.emily.infrastructure.logback.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class LogbackAsyncAppenderTest {

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
    void shouldUseLogbackDefaultDiscardingThresholdWhenUnset() {
        LogbackProperties properties = properties(100);
        AsyncAppender appender = factory(properties).getOrCreate(startedTarget());

        Assertions.assertEquals(20, appender.getDiscardingThreshold());
    }

    @Test
    void shouldAcceptZeroDiscardingThreshold() {
        LogbackProperties properties = properties(100);
        properties.getAsync().setDiscardingThreshold(0);
        AsyncAppender appender = factory(properties).getOrCreate(startedTarget());

        Assertions.assertEquals(0, appender.getDiscardingThreshold());
    }

    @Test
    void shouldConfigureCallerDataCollection() {
        LogbackProperties defaultProperties = properties(100);
        AsyncAppender defaultAppender = factory(defaultProperties).getOrCreate(startedTarget());
        Assertions.assertFalse(defaultAppender.isIncludeCallerData());

        LogbackProperties enabledProperties = properties(100);
        enabledProperties.getAsync().setIncludeCallerData(true);
        AsyncAppender enabledAppender = factory(enabledProperties).getOrCreate(startedTarget());
        Assertions.assertTrue(enabledAppender.isIncludeCallerData());
    }

    @Test
    void shouldRejectInvalidQueueSize() {
        LogbackProperties properties = properties(0);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory(properties).getOrCreate(startedTarget()));
    }

    @Test
    void shouldRejectNegativeMaxFlushTime() {
        LogbackProperties properties = properties(100);
        properties.getAsync().setMaxFlushTime(-1);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory(properties).getOrCreate(startedTarget()));
    }

    @Test
    void shouldRejectDiscardingThresholdOutsideQueueRange() {
        LogbackProperties negative = properties(100);
        negative.getAsync().setDiscardingThreshold(-1);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory(negative).getOrCreate(startedTarget()));

        LogbackProperties greaterThanQueue = properties(100);
        greaterThanQueue.getAsync().setDiscardingThreshold(101);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> factory(greaterThanQueue).getOrCreate(startedTarget()));
    }

    @Test
    void shouldRejectInvalidTargetAppender() {
        LogbackAsyncAppender factory = factory(properties(100));
        Assertions.assertThrows(NullPointerException.class, () -> factory.getOrCreate(null));

        ListAppender<ILoggingEvent> unnamed = new ListAppender<>();
        unnamed.setContext(context);
        unnamed.start();
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.getOrCreate(unnamed));

        ListAppender<ILoggingEvent> stopped = new ListAppender<>();
        stopped.setContext(context);
        stopped.setName("stopped-" + UUID.randomUUID());
        Assertions.assertThrows(IllegalStateException.class, () -> factory.getOrCreate(stopped));
    }

    @Test
    void shouldExposeAsyncAppenderQueueSnapshot() {
        LogbackProperties properties = properties(100);
        properties.getAsync().setDiscardingThreshold(10);
        properties.getAsync().setNeverBlock(true);
        LogbackAsyncAppender factory = factory(properties);
        AsyncAppender appender = factory.getOrCreate(startedTarget());

        AsyncAppenderQueueSnapshot snapshot = factory.getQueueSnapshots().getFirst();

        Assertions.assertEquals(appender.getName(), snapshot.name());
        Assertions.assertTrue(snapshot.started());
        Assertions.assertEquals(100, snapshot.queueSize());
        Assertions.assertEquals(snapshot.queueSize(), snapshot.queuedElements() + snapshot.remainingCapacity());
        Assertions.assertEquals((double) snapshot.queuedElements() / snapshot.queueSize(), snapshot.usageRatio());
        Assertions.assertEquals(10, snapshot.discardingThreshold());
        Assertions.assertTrue(snapshot.neverBlock());
    }

    private LogbackAsyncAppender factory(LogbackProperties properties) {
        return new LogbackAsyncAppender(context, properties);
    }

    private LogbackProperties properties(int queueSize) {
        LogbackProperties properties = new LogbackProperties();
        properties.getAsync().setQueueSize(queueSize);
        return properties;
    }

    private ListAppender<ILoggingEvent> startedTarget() {
        ListAppender<ILoggingEvent> target = new ListAppender<>();
        target.setContext(context);
        target.setName("target-" + UUID.randomUUID());
        target.start();
        return target;
    }
}
