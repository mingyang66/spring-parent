package com.emily.infrastructure.logback.configuration.context;

import ch.qos.logback.classic.LoggerContext;
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
import org.slf4j.Logger;

import java.util.Objects;

/**
 * Logback日志上下文，负责组件注册、Logger创建及生命周期管理。
 *
 * @author Emily
 * @since 2020/08/04
 */
public class LogbackContext {
    private LoggerContext context;
    private LogbackProperties properties;

    /**
     * 初始化日志上下文。
     * <p>依次完成：属性验证、状态快照、组件注册、TurboFilter安装、Root Logger初始化。
     * 初始化失败时自动回滚至快照状态。
     *
     * @param context    logback LoggerContext
     * @param properties 日志配置属性
     */
    public synchronized void initialize(LoggerContext context, LogbackProperties properties) {
        this.context = Objects.requireNonNull(context, "LoggerContext must not be null");
        this.properties = Objects.requireNonNull(properties, "LogbackProperties must not be null");
        doInitialize();
        configure();
        initRootLogger();
    }

    private void doInitialize() {
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
    }

    private void configure() {
        new ConfigurationAction(context, properties).start();
        properties.getMarker().getAcceptMarker().forEach(marker ->
                context.addTurboFilter(LogBeanFactory.getComponent(LogAcceptMarkerFilter.class).getFilter(marker)));
        properties.getMarker().getDenyMarker().forEach(marker ->
                context.addTurboFilter(LogBeanFactory.getComponent(LogDenyMarkerFilter.class).getFilter(marker)));
    }

    private void initRootLogger() {
        LogPathField field = LogPathField.newBuilder()
                .withLoggerName(Logger.ROOT_LOGGER_NAME)
                .withFilePath(PathUtils.normalizePath(properties.getRoot().getFilePath()))
                .withLogbackType(LogbackType.ROOT)
                .build();
        LogBeanFactory.getOrCreateLogger(Logger.ROOT_LOGGER_NAME, () -> createLogger(field));
    }

    /**
     * 获取模块Logger，同名Logger通过双重检查保证只创建一次。
     *
     * @param requiredType 调用方类型
     * @param logbackType  日志类型
     * @param filePath     日志文件目录
     * @param fileName     日志文件名
     * @param <T>          调用方泛型
     * @return SLF4J Logger实例
     */
    public <T> Logger getLogger(Class<T> requiredType, LogbackType logbackType, String filePath, String fileName) {
        LogPathField field = LogPathField.newBuilder()
                .withLoggerName(LogNameUtils.joinLogName(logbackType, filePath, fileName, requiredType))
                .withFilePath(PathUtils.normalizePath(filePath))
                .withFileName(fileName)
                .withLogbackType(logbackType)
                .build();
        return LogBeanFactory.getOrCreateLogger(field.getLoggerName(), () -> createLogger(field));
    }

    private Logger createLogger(LogPathField field) {
        return LogBeanFactory.getComponents(Logback.class).stream()
                .filter(l -> l.supports(field.getLogbackType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Logback implementation found for " + field.getLogbackType()))
                .getLogger(field);
    }

    /**
     * 关闭SDK资源并恢复LoggerContext至初始化前状态。
     */
    public synchronized void stopAndReset() {
        LogBeanFactory.shutdownAndClear();
        context.stop();
    }
}
