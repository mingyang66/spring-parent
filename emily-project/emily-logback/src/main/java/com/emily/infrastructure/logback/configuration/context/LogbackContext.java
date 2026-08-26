package com.emily.infrastructure.logback.configuration.context;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.StatusListener;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.common.LogNameUtils;
import com.emily.infrastructure.logback.common.LogPathField;
import com.emily.infrastructure.logback.common.PathUtils;
import com.emily.infrastructure.logback.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackConsoleAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logback.configuration.classic.Logback;
import com.emily.infrastructure.logback.configuration.classic.LogbackGroup;
import com.emily.infrastructure.logback.configuration.classic.LogbackModule;
import com.emily.infrastructure.logback.configuration.classic.LogbackRoot;
import com.emily.infrastructure.logback.configuration.encoder.LogbackConsoleLayoutEncoder;
import com.emily.infrastructure.logback.configuration.encoder.LogbackPatternLayoutEncoder;
import com.emily.infrastructure.logback.configuration.filter.LogAcceptMarkerFilter;
import com.emily.infrastructure.logback.configuration.filter.LogDenyMarkerFilter;
import com.emily.infrastructure.logback.configuration.filter.LogLevelFilter;
import com.emily.infrastructure.logback.configuration.filter.LogThresholdLevelFilter;
import com.emily.infrastructure.logback.configuration.policy.LogbackFixedWindowRollingPolicy;
import com.emily.infrastructure.logback.configuration.policy.LogbackSizeAndTimeBasedRollingPolicy;
import com.emily.infrastructure.logback.configuration.policy.LogbackTimeBasedRollingPolicy;
import com.emily.infrastructure.logback.configuration.type.LogbackType;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import com.emily.infrastructure.logback.factory.LogbackPropertiesValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logback日志上下文，负责组件注册、Logger创建及生命周期管理。
 *
 * @author Emily
 * @since 2020/08/04
 */
public class LogbackContext {

    private static final Object INITIALIZATION_LOCK = new Object();
    private static final Object LOGGER_CREATION_LOCK = new Object();

    private final Map<String, LoggerSnapshot> loggerSnapshots = new ConcurrentHashMap<>();
    private boolean initialized;
    private InitializationSnapshot initializationSnapshot;

    /**
     * 初始化日志上下文。
     * <p>依次完成：属性验证、状态快照、组件注册、TurboFilter安装、Root Logger初始化。
     * 初始化失败时自动回滚至快照状态。
     *
     * @param context    logback LoggerContext
     * @param properties 日志配置属性
     */
    public void initialize(LoggerContext context, LogbackProperties properties) {
        Objects.requireNonNull(context, "context must not be null");
        LogbackPropertiesValidator.validate(properties);
        synchronized (INITIALIZATION_LOCK) {
            if (initialized) {
                throw new IllegalStateException("LogbackContext instance has already been initialized");
            }
            if (!LogBeanFactory.isEmpty()) {
                throw new IllegalStateException("Logback component container is not empty");
            }
            InitializationSnapshot snapshot = InitializationSnapshot.capture(context);
            try {
                doInitialize(context, properties);
                initializationSnapshot = snapshot;
                initialized = true;
            } catch (RuntimeException | Error ex) {
                try {
                    LogBeanFactory.shutdownAndClear();
                } catch (RuntimeException cleanupEx) {
                    ex.addSuppressed(cleanupEx);
                }
                try {
                    snapshot.restore(context);
                } catch (RuntimeException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }
                throw ex;
            }
        }
    }

    private void doInitialize(LoggerContext context, LogbackProperties properties) {
        // 核心组件
        LogBeanFactory.registerComponent(LogbackGroup.class, new LogbackGroup(context, properties));
        LogBeanFactory.registerComponent(LogbackModule.class, new LogbackModule(context, properties));
        LogBeanFactory.registerComponent(LogbackRoot.class, new LogbackRoot(context, properties));
        // Appender
        LogBeanFactory.registerComponent(LogbackAsyncAppender.class, new LogbackAsyncAppender(context, properties));
        LogBeanFactory.registerComponent(LogbackConsoleAppender.class, new LogbackConsoleAppender(context, properties));
        LogBeanFactory.registerComponent(LogbackRollingFileAppender.class, new LogbackRollingFileAppender(context, properties));
        // 滚动策略
        LogBeanFactory.registerComponent(LogbackSizeAndTimeBasedRollingPolicy.class, new LogbackSizeAndTimeBasedRollingPolicy(context, properties));
        LogBeanFactory.registerComponent(LogbackTimeBasedRollingPolicy.class, new LogbackTimeBasedRollingPolicy(context, properties));
        LogBeanFactory.registerComponent(LogbackFixedWindowRollingPolicy.class, new LogbackFixedWindowRollingPolicy(context, properties));
        // 编码器
        LogBeanFactory.registerComponent(LogbackPatternLayoutEncoder.class, new LogbackPatternLayoutEncoder(context));
        LogBeanFactory.registerComponent(LogbackConsoleLayoutEncoder.class, new LogbackConsoleLayoutEncoder(context));
        // 过滤器
        LogBeanFactory.registerComponent(LogAcceptMarkerFilter.class, new LogAcceptMarkerFilter(context));
        LogBeanFactory.registerComponent(LogDenyMarkerFilter.class, new LogDenyMarkerFilter(context));
        LogBeanFactory.registerComponent(LogLevelFilter.class, new LogLevelFilter(context));
        LogBeanFactory.registerComponent(LogThresholdLevelFilter.class, new LogThresholdLevelFilter(context));

        new ConfigurationAction(context, properties).start();
        properties.getMarker().getAcceptMarker().forEach(marker ->
                context.addTurboFilter(LogBeanFactory.getComponent(LogAcceptMarkerFilter.class).getFilter(marker)));
        properties.getMarker().getDenyMarker().forEach(marker ->
                context.addTurboFilter(LogBeanFactory.getComponent(LogDenyMarkerFilter.class).getFilter(marker)));
        initRootLogger(properties);
    }

    /**
     * 获取模块Logger，同名Logger通过双重检查保证只创建一次。
     *
     * @param requiredType 调用方类型
     * @param filePath     日志文件目录
     * @param fileName     日志文件名
     * @param logbackType  日志类型
     * @param <T>          调用方泛型
     * @return SLF4J Logger实例
     */
    public <T> Logger getLogger(Class<T> requiredType, String filePath, String fileName, LogbackType logbackType) {
        LogPathField field = LogPathField.newBuilder()
                .withLoggerName(LogNameUtils.joinLogName(logbackType, filePath, fileName, requiredType))
                .withFilePath(PathUtils.normalizePath(filePath))
                .withFileName(fileName)
                .withLogbackType(logbackType)
                .build();
        String loggerName = field.getLoggerName();
        Logger logger = LogBeanFactory.getLogger(loggerName);
        if (logger != null) {
            return logger;
        }
        synchronized (LOGGER_CREATION_LOCK) {
            logger = LogBeanFactory.getLogger(loggerName);
            if (logger == null) {
                logger = createLogger(field, logbackType);
                LogBeanFactory.registerLogger(loggerName, logger);
            }
            return logger;
        }
    }

    private Logger createLogger(LogPathField field, LogbackType logbackType) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = context.getLogger(field.getLoggerName());
        loggerSnapshots.putIfAbsent(field.getLoggerName(), LoggerSnapshot.capture(logger));
        return LogBeanFactory.getComponents(Logback.class).stream()
                .filter(l -> l.supports(logbackType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Logback implementation found for " + logbackType))
                .getLogger(field);
    }

    /**
     * 关闭SDK资源并恢复LoggerContext至初始化前状态。
     */
    public void shutdown() {
        synchronized (INITIALIZATION_LOCK) {
            if (!initialized) {
                return;
            }
            RuntimeException failure = null;
            try {
                LogBeanFactory.shutdownAndClear();
            } catch (RuntimeException ex) {
                failure = ex;
            }
            try {
                for (LoggerSnapshot snapshot : loggerSnapshots.values()) {
                    snapshot.restore();
                }
                LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
                initializationSnapshot.restore(context);
            } catch (RuntimeException ex) {
                if (failure == null) {
                    failure = ex;
                } else {
                    failure.addSuppressed(ex);
                }
            } finally {
                loggerSnapshots.clear();
                initializationSnapshot = null;
                initialized = false;
            }
            if (failure != null) {
                throw failure;
            }
        }
    }

    void initRootLogger(LogbackProperties properties) {
        LogPathField field = LogPathField.newBuilder()
                .withLoggerName(Logger.ROOT_LOGGER_NAME)
                .withFilePath(PathUtils.normalizePath(properties.getRoot().getFilePath()))
                .withLogbackType(LogbackType.ROOT)
                .build();
        Logger rootLogger = LogBeanFactory.getComponents(Logback.class).stream()
                .filter(l -> l.supports(LogbackType.ROOT))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Logback implementation found for " + LogbackType.ROOT))
                .getLogger(field);
        LogBeanFactory.registerLogger(Logger.ROOT_LOGGER_NAME, rootLogger);
    }

    private record InitializationSnapshot(
            ch.qos.logback.classic.Logger root,
            Level level,
            boolean additive,
            boolean packagingDataEnabled,
            Set<Appender<ILoggingEvent>> appenders,
            Set<TurboFilter> turboFilters,
            Set<StatusListener> statusListeners) {

        static InitializationSnapshot capture(LoggerContext context) {
            ch.qos.logback.classic.Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
            return new InitializationSnapshot(
                    root,
                    root.getLevel(),
                    root.isAdditive(),
                    context.isPackagingDataEnabled(),
                    identitySet(root.iteratorForAppenders()),
                    identitySet(context.getTurboFilterList().iterator()),
                    identitySet(context.getStatusManager().getCopyOfStatusListenerList().iterator()));
        }

        void restore(LoggerContext context) {
            Set<Appender<ILoggingEvent>> currentAppenders = identitySet(root.iteratorForAppenders());
            for (Appender<ILoggingEvent> appender : currentAppenders) {
                if (!appenders.contains(appender)) {
                    root.detachAppender(appender);
                }
            }
            for (Appender<ILoggingEvent> appender : appenders) {
                if (!root.isAttached(appender)) {
                    root.addAppender(appender);
                }
            }
            root.setLevel(level);
            root.setAdditive(additive);
            context.setPackagingDataEnabled(packagingDataEnabled);

            for (TurboFilter filter : new ArrayList<>(context.getTurboFilterList())) {
                if (!turboFilters.contains(filter)) {
                    context.getTurboFilterList().remove(filter);
                    filter.stop();
                }
            }
            for (StatusListener listener : context.getStatusManager().getCopyOfStatusListenerList()) {
                if (!statusListeners.contains(listener)) {
                    context.getStatusManager().remove(listener);
                    if (listener instanceof LifeCycle lifeCycle && lifeCycle.isStarted()) {
                        lifeCycle.stop();
                    }
                }
            }
        }

        private static <T> Set<T> identitySet(Iterator<T> iterator) {
            Set<T> values = Collections.newSetFromMap(new IdentityHashMap<>());
            iterator.forEachRemaining(values::add);
            return values;
        }
    }

    private record LoggerSnapshot(
            ch.qos.logback.classic.Logger logger,
            Level level,
            boolean additive,
            Set<Appender<ILoggingEvent>> appenders) {

        static LoggerSnapshot capture(ch.qos.logback.classic.Logger logger) {
            return new LoggerSnapshot(
                    logger,
                    logger.getLevel(),
                    logger.isAdditive(),
                    InitializationSnapshot.identitySet(logger.iteratorForAppenders()));
        }

        void restore() {
            Set<Appender<ILoggingEvent>> currentAppenders = InitializationSnapshot.identitySet(logger.iteratorForAppenders());
            for (Appender<ILoggingEvent> appender : currentAppenders) {
                if (!appenders.contains(appender)) {
                    logger.detachAppender(appender);
                }
            }
            for (Appender<ILoggingEvent> appender : appenders) {
                if (!logger.isAttached(appender)) {
                    logger.addAppender(appender);
                }
            }
            logger.setLevel(level);
            logger.setAdditive(additive);
        }
    }
}
