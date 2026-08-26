package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.common.StrUtils;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import com.emily.infrastructure.logback.factory.LogbackPropertiesValidator;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 异步Appender管理器，负责创建和注册异步包装Appender。
 *
 * @author Emily
 * @since 2020/08/04
 */
public class LogbackAsyncAppender {

    public static final String PREFIX = "ASYNC-";

    private final LoggerContext context;
    private final LogbackProperties properties;

    public LogbackAsyncAppender(LoggerContext context, LogbackProperties properties) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    public AsyncAppender getOrCreate(Appender<ILoggingEvent> ref) {
        String appenderName = validateAndGetName(ref);
        return (AsyncAppender) LogBeanFactory.getOrCreateAppender(appenderName,
                name -> createAppender(ref, name));
    }

    /**
     * 获取所有已注册异步Appender的实时队列状态快照。
     *
     * @return 按Appender名称排序的队列状态快照
     */
    public List<AsyncAppenderQueueSnapshot> getQueueSnapshots() {
        return LogBeanFactory.<AsyncAppender>getAppenders(AsyncAppender.class).stream()
                .map(AsyncAppenderQueueSnapshot::from)
                .sorted(Comparator.comparing(AsyncAppenderQueueSnapshot::name))
                .toList();
    }

    private AsyncAppender createAppender(Appender<ILoggingEvent> ref, String appenderName) {
        LogbackProperties.Async async = properties.getAsync();
        LogbackPropertiesValidator.validateAsync(async);
        MonitoredAsyncAppender appender = new MonitoredAsyncAppender();
        appender.setContext(context);
        appender.setName(appenderName);
        appender.setQueueSize(async.getQueueSize());
        Integer discardingThreshold = async.getDiscardingThreshold();
        if (discardingThreshold != null) {
            appender.setDiscardingThreshold(discardingThreshold);
        }
        appender.setIncludeCallerData(async.isIncludeCallerData());
        appender.setMaxFlushTime(async.getMaxFlushTime());
        appender.setNeverBlock(async.isNeverBlock());
        appender.addAppender(ref);
        appender.start();
        if (!appender.isStarted()) {
            appender.detachAppender(ref);
            throw new IllegalStateException("Failed to start async appender " + appenderName);
        }
        return appender;
    }

    private String validateAndGetName(Appender<ILoggingEvent> ref) {
        Objects.requireNonNull(ref, "ref must not be null");
        String refName = ref.getName();
        if (refName == null || refName.isBlank()) {
            throw new IllegalArgumentException("ref name must not be blank");
        }
        if (!ref.isStarted()) {
            throw new IllegalStateException("Referenced appender is not started: " + refName);
        }
        return StrUtils.join(PREFIX, refName);
    }
}
