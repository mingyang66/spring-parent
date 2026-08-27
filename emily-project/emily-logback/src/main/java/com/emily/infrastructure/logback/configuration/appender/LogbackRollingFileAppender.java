package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.common.LogPathField;
import com.emily.infrastructure.logback.common.PathUtils;
import com.emily.infrastructure.logback.common.StrUtils;
import com.emily.infrastructure.logback.configuration.encoder.LogbackPatternLayoutEncoder;
import com.emily.infrastructure.logback.configuration.filter.LogLevelFilter;
import com.emily.infrastructure.logback.configuration.policy.LogbackRollingPolicy;
import com.emily.infrastructure.logback.configuration.type.LogbackType;
import com.emily.infrastructure.logback.configuration.type.RollingPolicyType;
import com.emily.infrastructure.logback.factory.LogBeanFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * 滚动文件Appender管理器，负责创建和注册基于时间和大小的滚动文件Appender。
 *
 * @author Emily
 * @since 2020/08/04
 */
public class LogbackRollingFileAppender {

    private final LoggerContext context;
    private final LogbackProperties properties;

    public LogbackRollingFileAppender(LoggerContext context, LogbackProperties properties) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    public Appender<ILoggingEvent> getOrCreate(Level level, LogPathField field) {
        Objects.requireNonNull(level, "level must not be null");
        Objects.requireNonNull(field, "field must not be null");
        String appenderName = getName(level, field);
        return LogBeanFactory.getOrCreateAppender(appenderName,
                name -> createAppender(level, field));
    }

    private RollingFileAppender<ILoggingEvent> createAppender(Level level, LogPathField field) {
        String loggerPath = getFilePath(level, field);
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        RollingPolicy rollingPolicy = this.getRollingPolicy(appender, loggerPath);
        appender.setContext(context);
        appender.setName(getName(level, field));
        appender.setFile(loggerPath);
        appender.setRollingPolicy(rollingPolicy);
        appender.setAppend(properties.getAppender().isAppend());
        appender.setPrudent(properties.getAppender().isPrudent());
        appender.addFilter(LogBeanFactory.getComponent(LogLevelFilter.class).getFilter(level));
        appender.setEncoder(LogBeanFactory.getComponent(LogbackPatternLayoutEncoder.class).getEncoder(getFilePattern(field)));
        appender.setImmediateFlush(properties.getAppender().isImmediateFlush());
        appender.start();
        if (!appender.isStarted()) {
            throw new IllegalStateException("Failed to start rolling file appender " + appender.getName() + " for " + loggerPath);
        }
        return appender;
    }

    private RollingPolicy getRollingPolicy(RollingFileAppender<ILoggingEvent> appender, String loggerPath) {
        RollingPolicyType policyType = properties.getAppender().getRollingPolicyType();
        return LogBeanFactory.getComponents(LogbackRollingPolicy.class).stream()
                .filter(l -> l.support(policyType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No rolling policy found for " + policyType))
                .getRollingPolicy(appender, loggerPath);
    }

    private String getFilePath(Level level, LogPathField field) {
        String basePath = properties.getAppender().getPath();
        String filePath = field.getFilePath();
        String levelStr = level.levelStr.toLowerCase();
        String loggerPath = StrUtils.join(basePath, filePath, File.separator);

        if (LogbackType.ROOT == field.getLogbackType() || LogbackType.GROUP == field.getLogbackType()) {
            loggerPath = StrUtils.join(loggerPath, levelStr, File.separator, levelStr);
        } else if (LogbackType.MODULE == field.getLogbackType()) {
            loggerPath = StrUtils.join(loggerPath, field.getFileName());
        } else {
            throw new UnsupportedOperationException("Unsupported log type");
        }
        String resolvedPath = StrUtils.substVars(context, loggerPath, ".log")
                .replace('/', File.separatorChar)
                .replace('\\', File.separatorChar);
        return Path.of(resolvedPath).normalize().toString();
    }

    private String getFilePattern(LogPathField field) {
        if (LogbackType.ROOT.equals(field.getLogbackType())) {
            return properties.getRoot().getPattern();
        }
        if (LogbackType.GROUP.equals(field.getLogbackType())) {
            return properties.getGroup().getPattern();
        }
        return properties.getModule().getPattern();
    }

    private String getName(Level level, LogPathField field) {
        String fileName = field.getFileName();
        if (StrUtils.isEmpty(fileName)) {
            fileName = level.levelStr.toLowerCase();
        }
        String levelStr = level.levelStr.toLowerCase();
        return (field.getLogbackType() + field.getFilePath() + "." + fileName + "." + levelStr)
                .replace(PathUtils.SLASH, PathUtils.DOT);
    }
}
