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

import java.util.*;

/**
 * 日志类 logback+slf4j
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class LogbackContext {
    private static final Object INITIALIZATION_LOCK = new Object();
    private static final Object LOGGER_CREATION_LOCK = new Object();
    private final Map<String, LoggerSnapshot> loggerSnapshots = new HashMap<>();
    private boolean initialized;
    private LoggerContext loggerContext;
    private InitializationSnapshot initializationSnapshot;

    /**
     * ------------------------------------
     * 1. 属性配置
     * 2. 报告状态展示控制；
     * 3. debug内部状态信息控制；
     * 4. packagingData异常堆栈拼接所属jar包控制
     * 5. 全局过滤器TurboFilter控制
     *
     * @param context    上下文
     * @param properties logback日志属性
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
                loggerContext = context;
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
        // 注册日志对象
        LogBeanFactory.registerComponent(LogbackGroup.class, new LogbackGroup(context, properties));
        LogBeanFactory.registerComponent(LogbackModule.class, new LogbackModule(context, properties));
        LogBeanFactory.registerComponent(LogbackRoot.class, new LogbackRoot(context, properties));

        LogBeanFactory.registerComponent(LogbackAsyncAppender.class, new LogbackAsyncAppender(context, properties));
        LogBeanFactory.registerComponent(LogbackConsoleAppender.class, new LogbackConsoleAppender(context, properties));
        LogBeanFactory.registerComponent(LogbackRollingFileAppender.class, new LogbackRollingFileAppender(context, properties));

        LogBeanFactory.registerComponent(LogbackSizeAndTimeBasedRollingPolicy.class, new LogbackSizeAndTimeBasedRollingPolicy(context, properties));
        LogBeanFactory.registerComponent(LogbackTimeBasedRollingPolicy.class, new LogbackTimeBasedRollingPolicy(context, properties));
        LogBeanFactory.registerComponent(LogbackFixedWindowRollingPolicy.class, new LogbackFixedWindowRollingPolicy(context, properties));

        LogBeanFactory.registerComponent(LogbackPatternLayoutEncoder.class, new LogbackPatternLayoutEncoder(context));
        LogBeanFactory.registerComponent(LogbackConsoleLayoutEncoder.class, new LogbackConsoleLayoutEncoder(context));

        LogBeanFactory.registerComponent(LogAcceptMarkerFilter.class, new LogAcceptMarkerFilter(context));
        LogBeanFactory.registerComponent(LogDenyMarkerFilter.class, new LogDenyMarkerFilter(context));
        LogBeanFactory.registerComponent(LogLevelFilter.class, new LogLevelFilter(context));
        LogBeanFactory.registerComponent(LogThresholdLevelFilter.class, new LogThresholdLevelFilter(context));
        //开启OnConsoleStatusListener监听器，即开启debug模式
        new ConfigurationAction(context, properties).start();
        //全局过滤器，接受指定标记的日志记录到文件中
        properties.getMarker().getAcceptMarker().forEach((marker) -> context.addTurboFilter(LogBeanFactory.getComponent(LogAcceptMarkerFilter.class).getFilter(marker)));
        //全局过滤器，拒绝标记的日志记录到文件中
        properties.getMarker().getDenyMarker().forEach((marker) -> context.addTurboFilter(LogBeanFactory.getComponent(LogDenyMarkerFilter.class).getFilter(marker)));
        //初始化Root Logger
        initRootLogger(properties);
    }

    /**
     * 获取logger日志对象，同名Logger使用双重检查确保只初始化一次。
     *
     * @param requiredType 当前打印类实例
     * @param filePath     文件路径
     * @param fileName     文件名称
     * @param logbackType  日志类型
     * @param <T>          类类型
     * @return logger对象
     */
    public <T> Logger getLogger(Class<T> requiredType, String filePath, String fileName, LogbackType logbackType) {
        //通用参数
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
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(field.getLoggerName());
        loggerSnapshots.putIfAbsent(field.getLoggerName(), LoggerSnapshot.capture(logger));
        return LogBeanFactory.getComponents(Logback.class).stream()
                .filter(l -> l.supports(logbackType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Logback implementation found for " + logbackType))
                .getLogger(field);
    }

    /**
     * 停止SDK资源并恢复初始化前及运行期间接管前的LoggerContext状态。
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
                initializationSnapshot.restore(loggerContext);
            } catch (RuntimeException ex) {
                if (failure == null) {
                    failure = ex;
                } else {
                    failure.addSuppressed(ex);
                }
            } finally {
                loggerSnapshots.clear();
                initializationSnapshot = null;
                loggerContext = null;
                initialized = false;
            }
            if (failure != null) {
                throw failure;
            }
        }
    }

    /**
     * 启动上下文，初始化root logger对象
     */
    void initRootLogger(LogbackProperties properties) {
        // 获取root logger对象
        Logger rootLogger = LogBeanFactory.getComponents(Logback.class).stream().filter(l -> l.supports(LogbackType.ROOT)).findFirst().orElseThrow().getLogger(LogPathField.newBuilder()
                // logger name
                .withLoggerName(Logger.ROOT_LOGGER_NAME)
                // logger file path
                .withFilePath(PathUtils.normalizePath(properties.getRoot().getFilePath()))
                // logger type
                .withLogbackType(LogbackType.ROOT)
                .build());
        // 将root添加到缓存
        LogBeanFactory.registerLogger(Logger.ROOT_LOGGER_NAME, rootLogger);
    }

    /**
     * 初始化回滚
     */
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
            for (Appender<ILoggingEvent> appender : identitySet(root.iteratorForAppenders())) {
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
            for (Appender<ILoggingEvent> appender : InitializationSnapshot.identitySet(logger.iteratorForAppenders())) {
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
