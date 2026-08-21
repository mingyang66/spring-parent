package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.common.StrUtils;
import com.emily.infrastructure.logback.factory.LogBeanFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 设置异步Appender
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
    private final LoggerContext context;
    /**
     * 属性配置
     */
    private final LogbackProperties properties;

    public LogbackAsyncAppender(LoggerContext context, LogbackProperties properties) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    /**
     * 创建并注册Logback异步Appender。
     * 每个异步Appender包装一个目标Appender，并使用独立有界队列
     * 和单个Worker线程异步转发日志事件
     *
     * @param ref 附件appender的引用
     * @return 异步appender对象
     */
    private AsyncAppender getAppender(Appender<ILoggingEvent> ref) {
        String appenderName = validateAndGetName(ref);
        LogbackProperties.Async async = properties.getAsync();
        if (async.getQueueSize() < 1) {
            throw new IllegalArgumentException("Async appender queueSize must be greater than 0");
        }
        if (async.getMaxFlushTime() < 0) {
            throw new IllegalArgumentException("Async appender maxFlushTime must not be negative");
        }
        Integer discardingThreshold = async.getDiscardingThreshold();
        if (Objects.nonNull(discardingThreshold)
                && (discardingThreshold < 0 || discardingThreshold > async.getQueueSize())) {
            throw new IllegalArgumentException("Async appender discardingThreshold must be between 0 and queueSize");
        }
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        AsyncAppender appender = getAsyncAppender(appenderName, async, discardingThreshold);
        //添加附加的appender,最多只能添加一个
        appender.addAppender(ref);
        appender.start();
        if (!appender.isStarted()) {
            appender.detachAppender(ref);
            throw new IllegalStateException("Failed to start async appender " + appender.getName());
        }
        return appender;
    }

    private AsyncAppender getAsyncAppender(String appenderName, LogbackProperties.Async async, Integer discardingThreshold) {
        AsyncAppender appender = new AsyncAppender();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setContext(context);
        //appender的name属性
        appender.setName(appenderName);
        //设置异步日志事件队列容量
        appender.setQueueSize(async.getQueueSize());
        //默认，当队列还剩余 20% 的容量时，会丢弃级别为 TRACE, DEBUG 与 INFO 的日志，仅仅只保留 WARN 与 ERROR 级别的日志。想要保留所有的事件，可以设置为 0
        if (Objects.nonNull(discardingThreshold)) {
            appender.setDiscardingThreshold(discardingThreshold);
        }
        //获取调用者的数据相对来说比较昂贵。为了提高性能，默认情况下不会获取调用者的信息。默认情况下，只有像线程名或者 MDC 这种"便宜"的数据会被复制。设置为 true 时，appender 会包含调用者的信息
        appender.setIncludeCallerData(async.isIncludeCallerData());
        //根据所引用 appender 队列的深度以及延迟， AsyncAppender 可能会耗费长时间去刷新队列。当 LoggerContext 被停止时，
        // AsyncAppender stop 方法会等待工作线程指定的时间来完成。使用 maxFlushTime 来指定最大的刷新时间，单位为毫秒。在指定时间内没有被处理完的事件将会被丢弃。
        // 这个属性的值的含义与 Thread.join(long)) 相同
        appender.setMaxFlushTime(async.getMaxFlushTime());
        //默认为 false，在队列满的时候 appender 会阻塞而不是丢弃信息。设置为 true，appender 不会阻塞你的应用而会将消息丢弃
        appender.setNeverBlock(async.isNeverBlock());
        return appender;
    }

    public AsyncAppender getOrCreate(Appender<ILoggingEvent> ref) {
        String appenderName = validateAndGetName(ref);
        return LogBeanFactory.computeIfAbsent(appenderName, key -> getAppender(ref));
    }

    /**
     * 获取所有已注册异步Appender的实时队列状态快照。
     *
     * @return 按Appender名称排序的队列状态快照
     */
    public List<AsyncAppenderQueueSnapshot> getQueueSnapshots() {
        return LogBeanFactory.getBeans(AsyncAppender.class).stream()
                .map(AsyncAppenderQueueSnapshot::from)
                .sorted(Comparator.comparing(AsyncAppenderQueueSnapshot::name))
                .toList();
    }

    private String validateAndGetName(Appender<ILoggingEvent> ref) {
        Objects.requireNonNull(ref, "ref must not be null");
        String refName = ref.getName();
        if (Objects.isNull(refName) || refName.isBlank()) {
            throw new IllegalArgumentException("ref name must not be blank");
        }
        if (!ref.isStarted()) {
            throw new IllegalStateException("Referenced appender is not started: " + refName);
        }
        return getName(refName);
    }

    private String getName(String name) {
        return StrUtils.join(PREFIX, name);
    }
}
