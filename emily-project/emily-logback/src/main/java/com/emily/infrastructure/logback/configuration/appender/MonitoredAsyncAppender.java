package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 在neverBlock模式下协调容量检查与非阻塞入队的异步Appender。
 */
public class MonitoredAsyncAppender extends AsyncAppender {
    private final Object nonBlockingEnqueueMonitor = new Object();

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
                return;
            }
            super.append(eventObject);
        }
    }
}
