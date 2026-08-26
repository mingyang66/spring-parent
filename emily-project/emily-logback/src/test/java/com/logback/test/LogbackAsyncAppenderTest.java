package com.logback.test;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.read.ListAppender;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.LogbackContextInitializer;
import com.emily.infrastructure.logback.configuration.appender.AsyncAppenderQueueSnapshot;
import com.emily.infrastructure.logback.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.configuration.filter.LogLevelFilter;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LogbackAsyncAppenderTest {

    private LoggerContext context;

    @BeforeEach
    void setUp() {
        context = new LoggerContext();
        context.setMDCAdapter(new LogbackMDCAdapter());
        LogBeanFactory.registerComponent(LogLevelFilter.class, new LogLevelFilter(context));
    }

    @AfterEach
    void tearDown() {
        LogbackContextInitializer.shutdown();
        context.stop();
    }


    @Test
    void shouldKeepLowLevelLogsWhenDiscardingThresholdIsZero() {
        LogbackProperties properties = properties(100);
        properties.getAsync().setDiscardingThreshold(0);

        AsyncAppender appender = factory(properties).getOrCreate(startedTarget(), Level.INFO);

        Assertions.assertEquals(0, appender.getDiscardingThreshold());
    }

    @Test
    void shouldConfigureCallerDataCollection() {
        LogbackProperties defaultProperties = properties(100);
        AsyncAppender defaultAppender = factory(defaultProperties).getOrCreate(startedTarget(), Level.INFO);
        Assertions.assertFalse(defaultAppender.isIncludeCallerData());

        LogbackProperties enabledProperties = properties(100);
        enabledProperties.getAsync().setIncludeCallerData(true);
        AsyncAppender enabledAppender = factory(enabledProperties).getOrCreate(startedTarget(), Level.INFO);
        Assertions.assertTrue(enabledAppender.isIncludeCallerData());
    }

    @Test
    void shouldRejectInvalidTargetAppender() {
        LogbackAsyncAppender factory = factory(properties(100));
        Assertions.assertThrows(NullPointerException.class, () -> factory.getOrCreate(null, Level.INFO));

        ListAppender<ILoggingEvent> unnamed = new ListAppender<>();
        unnamed.setContext(context);
        unnamed.start();
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.getOrCreate(unnamed, Level.INFO));

        ListAppender<ILoggingEvent> stopped = new ListAppender<>();
        stopped.setContext(context);
        stopped.setName("stopped-" + UUID.randomUUID());
        Assertions.assertThrows(IllegalStateException.class, () -> factory.getOrCreate(stopped, Level.INFO));
    }

    @Test
    void shouldExposeAsyncAppenderQueueSnapshot() {
        LogbackProperties properties = properties(100);
        properties.getAsync().setDiscardingThreshold(10);
        properties.getAsync().setNeverBlock(true);
        LogbackAsyncAppender factory = factory(properties);
        AsyncAppender appender = factory.getOrCreate(startedTarget(), Level.INFO);

        AsyncAppenderQueueSnapshot snapshot = factory.getQueueSnapshots().getFirst();

        Assertions.assertEquals(appender.getName(), snapshot.name());
        Assertions.assertTrue(snapshot.started());
        Assertions.assertEquals(100, snapshot.queueSize());
        Assertions.assertEquals(snapshot.queueSize(), snapshot.queuedElements() + snapshot.remainingCapacity());
        Assertions.assertEquals((double) snapshot.queuedElements() / snapshot.queueSize(), snapshot.usageRatio());
        Assertions.assertEquals(10, snapshot.discardingThreshold());
        Assertions.assertTrue(snapshot.neverBlock());
        Assertions.assertEquals(0, snapshot.enqueuedEvents());
        Assertions.assertEquals(0, snapshot.discardedEvents());
        Assertions.assertEquals(0, snapshot.rejectedEvents());
    }

    @Test
    void shouldCountDiscardedRejectedAndEnqueuedEvents() throws Exception {
        LogbackProperties properties = properties(4);
        properties.getAsync().setDiscardingThreshold(2);
        properties.getAsync().setNeverBlock(true);
        BlockingAppender target = new BlockingAppender();
        target.setContext(context);
        target.setName("blocking-" + UUID.randomUUID());
        target.start();
        LogbackAsyncAppender factory = factory(properties);
        AsyncAppender appender = factory.getOrCreate(target, Level.INFO);
        Logger logger = context.getLogger("monitored-async");
        logger.setLevel(Level.INFO);
        logger.setAdditive(false);
        logger.addAppender(appender);

        logger.info("worker-blocker");
        Assertions.assertTrue(target.awaitBlocked());
        logger.info("queued-1");
        logger.info("queued-2");
        logger.info("queued-3");
        logger.info("discarded");
        logger.warn("queued-warn");
        logger.warn("rejected-warn");

        AsyncAppenderQueueSnapshot snapshot = factory.getQueueSnapshots().getFirst();
        Assertions.assertEquals(4, snapshot.enqueuedEvents());
        Assertions.assertEquals(1, snapshot.discardedEvents());
        Assertions.assertEquals(0, snapshot.rejectedEvents());

        target.release();
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

    private static final class BlockingAppender extends AppenderBase<ILoggingEvent> {
        private final CountDownLatch blocked = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);

        @Override
        protected void append(ILoggingEvent eventObject) {
            blocked.countDown();
            try {
                release.await();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        boolean awaitBlocked() throws InterruptedException {
            return blocked.await(5, TimeUnit.SECONDS);
        }

        void release() {
            release.countDown();
        }
    }
}
