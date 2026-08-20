package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.AsyncAppender;

/**
 * 异步Appender队列运行状态快照。
 *
 * @param name                 Appender名称
 * @param started              是否已启动
 * @param queueSize            队列容量
 * @param queuedElements       当前排队事件数
 * @param remainingCapacity    队列剩余容量
 * @param usageRatio           队列使用率，取值范围为0到1
 * @param discardingThreshold  低级别日志丢弃阈值
 * @param neverBlock           队列满时是否不阻塞调用线程
 */
public record AsyncAppenderQueueSnapshot(
        String name,
        boolean started,
        int queueSize,
        int queuedElements,
        int remainingCapacity,
        double usageRatio,
        int discardingThreshold,
        boolean neverBlock) {

    static AsyncAppenderQueueSnapshot from(AsyncAppender appender) {
        int queueSize = appender.getQueueSize();
        int queuedElements = appender.getNumberOfElementsInQueue();
        double usageRatio = queueSize == 0 ? 0 : (double) queuedElements / queueSize;
        return new AsyncAppenderQueueSnapshot(
                appender.getName(),
                appender.isStarted(),
                queueSize,
                queuedElements,
                appender.getRemainingCapacity(),
                usageRatio,
                appender.getDiscardingThreshold(),
                appender.isNeverBlock());
    }
}
