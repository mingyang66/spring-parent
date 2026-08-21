package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.concurrent.atomic.LongAdder;

/**
 * 支持记录入队、阈值丢弃和队列满拒绝数量的异步Appender。
 */
public class MonitoredAsyncAppender extends AsyncAppender {
    private final Object nonBlockingEnqueueMonitor = new Object();
    private final LongAdder enqueuedEvents = new LongAdder();
    private final LongAdder discardedEvents = new LongAdder();
    private final LongAdder rejectedEvents = new LongAdder();

    @Override
    protected boolean isDiscardable(ILoggingEvent event) {
        boolean discardable = super.isDiscardable(event);
        if (discardable) {
            discardedEvents.increment();
        }
        return discardable;
    }

    @Override
    protected void preprocess(ILoggingEvent eventObject) {
        super.preprocess(eventObject);
        enqueuedEvents.increment();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isNeverBlock()) {
            super.append(eventObject);
            return;
        }
        synchronized (nonBlockingEnqueueMonitor) {
            if (isQueueBelowDiscardingThreshold() && isDiscardable(eventObject)) {
                return;
            }
            if (getRemainingCapacity() == 0) {
                rejectedEvents.increment();
                return;
            }
            super.append(eventObject);
        }
    }

    public long getEnqueuedEvents() {
        return enqueuedEvents.sum();
    }

    public long getDiscardedEvents() {
        return discardedEvents.sum();
    }

    public long getRejectedEvents() {
        return rejectedEvents.sum();
    }
}
