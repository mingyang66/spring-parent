package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.AsyncAppender;

/**
 * 异步Appender队列运行状态快照。
 * queuedElements仅统计BlockingQueue中的事件，不包含Worker已批量取出但尚未写入目标Appender的事件。
 * 各字段为非原子近似采样，不保证queuedElements与remainingCapacity之和始终等于queueSize。
 *
 * @param name                 Appender名称
 * @param started              是否已启动
 * @param queueSize            队列容量
 * @param queuedElements       当前排队事件数
 * @param remainingCapacity    队列剩余容量
 * @param usageRatio           队列使用率，取值范围为0到1
 * @param discardingThreshold  低级别日志丢弃阈值
 * @param neverBlock           队列满时是否不阻塞调用线程
 * @param enqueuedEvents        已成功入队的事件总数
 * @param discardedEvents       因丢弃阈值被丢弃的事件总数
 * @param rejectedEvents        因neverBlock且队列满被拒绝的事件总数
 */
public record AsyncAppenderQueueSnapshot(
        String name,
        boolean started,
        int queueSize,
        int queuedElements,
        int remainingCapacity,
        double usageRatio,
        int discardingThreshold,
        boolean neverBlock,
        long enqueuedEvents,
        long discardedEvents,
        long rejectedEvents) {

    static AsyncAppenderQueueSnapshot from(AsyncAppender appender) {
        int queueSize = appender.getQueueSize();
        int queuedElements = appender.getNumberOfElementsInQueue();
        double usageRatio = queueSize == 0 ? 0 : (double) queuedElements / queueSize;
        MonitoredAsyncAppender monitored = appender instanceof MonitoredAsyncAppender value ? value : null;
        return new AsyncAppenderQueueSnapshot(
                appender.getName(),
                appender.isStarted(),
                queueSize,
                queuedElements,
                appender.getRemainingCapacity(),
                usageRatio,
                appender.getDiscardingThreshold(),
                appender.isNeverBlock(),
                monitored == null ? 0 : monitored.getEnqueuedEvents(),
                monitored == null ? 0 : monitored.getDiscardedEvents(),
                monitored == null ? 0 : monitored.getRejectedEvents());
    }
}
