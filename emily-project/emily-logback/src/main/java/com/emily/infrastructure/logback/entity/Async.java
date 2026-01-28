package com.emily.infrastructure.logback.entity;

/**
 * @author :  Emily
 * @since :  2026/1/28 下午2:19
 */
public class Async {
    /**
     * 是否启用日志异步记录Appender
     */
    private boolean enabled;
    /**
     * 队列的最大容量，默认为 256
     */
    private int queueSize = 256;
    /**
     * 默认，当队列还剩余 20% 的容量时，会丢弃级别为 TRACE, DEBUG 与 INFO 的日志，仅仅只保留 WARN 与 ERROR 级别的日志。想要保留所有的事件，可以设置为 0
     */
    private int discardingThreshold;
    /**
     * 根据所引用 appender 队列的深度以及延迟， AsyncAppender 可能会耗费长时间去刷新队列。
     * 当 LoggerContext 被停止时， AsyncAppender stop 方法会等待工作线程指定的时间来完成。
     * 使用 maxFlushTime 来指定最大的刷新时间，单位为毫秒。在指定时间内没有被处理完的事件将会被丢弃。这个属性的值的含义与 Thread.join(long)) 相同
     * 默认是 1000毫秒
     */
    private int maxFlushTime = 1000;
    /**
     * 在队列满的时候 appender 会阻塞而不是丢弃信息。设置为 true，appender 不会阻塞你的应用而会将消息丢弃，默认为 false
     */
    private boolean neverBlock;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getDiscardingThreshold() {
        return discardingThreshold;
    }

    public void setDiscardingThreshold(int discardingThreshold) {
        this.discardingThreshold = discardingThreshold;
    }

    public int getMaxFlushTime() {
        return maxFlushTime;
    }

    public void setMaxFlushTime(int maxFlushTime) {
        this.maxFlushTime = maxFlushTime;
    }

    public boolean isNeverBlock() {
        return neverBlock;
    }

    public void setNeverBlock(boolean neverBlock) {
        this.neverBlock = neverBlock;
    }
}
