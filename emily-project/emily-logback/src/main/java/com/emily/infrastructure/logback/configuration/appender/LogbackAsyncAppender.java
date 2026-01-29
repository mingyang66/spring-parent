package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.emily.infrastructure.logback.LogbackProperties;

/**
 * 通过名字和级别设置异步Appender
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class LogbackAsyncAppender {
    /**
     * 前缀
     */
    public static final String PREFIX = "ASYNC-";
    /**
     * logger上下文
     */
    private final LoggerContext lc;
    /**
     * 属性配置
     */
    private final LogbackProperties properties;

    public LogbackAsyncAppender(LoggerContext lc, LogbackProperties properties) {
        this.lc = lc;
        this.properties = properties;
    }

    /**
     * 控制台打印appender
     *
     * @param ref 附件appender的引用
     * @return 异步appender对象
     */
    public Appender<ILoggingEvent> getAppender(Appender<ILoggingEvent> ref) {
        LogbackProperties.Async async = properties.getAppender().getAsync();
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        AsyncAppender appender = new AsyncAppender();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setContext(lc);
        //appender的name属性
        appender.setName(String.join("", PREFIX, ref.getName()));
        //队列的最大容量，默认为 256
        appender.setQueueSize(async.getQueueSize());
        //默认，当队列还剩余 20% 的容量时，会丢弃级别为 TRACE, DEBUG 与 INFO 的日志，仅仅只保留 WARN 与 ERROR 级别的日志。想要保留所有的事件，可以设置为 0
        appender.setDiscardingThreshold(async.getDiscardingThreshold());
        //获取调用者的数据相对来说比较昂贵。为了提高性能，默认情况下不会获取调用者的信息。默认情况下，只有像线程名或者 MDC 这种"便宜"的数据会被复制。设置为 true 时，appender 会包含调用者的信息
        appender.setIncludeCallerData(false);
        //根据所引用 appender 队列的深度以及延迟， AsyncAppender 可能会耗费长时间去刷新队列。当 LoggerContext 被停止时，
        // AsyncAppender stop 方法会等待工作线程指定的时间来完成。使用 maxFlushTime 来指定最大的刷新时间，单位为毫秒。在指定时间内没有被处理完的事件将会被丢弃。
        // 这个属性的值的含义与 Thread.join(long)) 相同
        appender.setMaxFlushTime(async.getMaxFlushTime());
        //默认为 false，在队列满的时候 appender 会阻塞而不是丢弃信息。设置为 true，appender 不会阻塞你的应用而会将消息丢弃
        appender.setNeverBlock(async.isNeverBlock());
        //添加附加的appender,最多只能添加一个
        appender.addAppender(ref);
        appender.start();
        return appender;
    }
}
