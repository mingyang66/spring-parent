package com.emily.infrastructure.logback.factory;

import ch.qos.logback.core.util.FileSize;
import com.emily.infrastructure.logback.LogbackProperties;

import java.util.Objects;

/**
 * 日志配置属性验证器。
 */
public final class LogbackPropertiesValidator {

    private static final String PREFIX = "spring.emily.logger.";

    private LogbackPropertiesValidator() {
    }

    public static void validate(LogbackProperties properties) {
        Objects.requireNonNull(properties, "properties must not be null");
        validateRoot(properties.getRoot());
        validateGroup(properties.getGroup());
        validateModule(properties.getModule());
        validateAppender(properties.getAppender());
        validateAsync(properties.getAsync());
    }

    private static void validateRoot(LogbackProperties.Root root) {
        requireNonNull(PREFIX + "root.level", root.getLevel());
        requireText(PREFIX + "root.file-path", root.getFilePath());
        requireText(PREFIX + "root.pattern", root.getPattern());
        requireText(PREFIX + "root.console-pattern", root.getConsolePattern());
    }

    private static void validateGroup(LogbackProperties.Group group) {
        requireNonNull(PREFIX + "group.level", group.getLevel());
        requireText(PREFIX + "group.pattern", group.getPattern());
    }

    private static void validateModule(LogbackProperties.Module module) {
        requireNonNull(PREFIX + "module.level", module.getLevel());
        requireText(PREFIX + "module.pattern", module.getPattern());
    }

    private static void validateAppender(LogbackProperties.Appender appender) {
        requireText(PREFIX + "appender.path", appender.getPath());
        requireNonNull(PREFIX + "appender.rolling-policy-type", appender.getRollingPolicyType());

        LogbackProperties.SizeTimeRollingPolicy sizeTime = appender.getSizeTimeRollingPolicy();
        requireNonNull(PREFIX + "appender.size-time-rolling-policy.compression-mode", sizeTime.getCompressionMode());
        requireNonNegative(PREFIX + "appender.size-time-rolling-policy.max-history", sizeTime.getMaxHistory());
        FileSize maxFileSize = parseFileSize(PREFIX + "appender.size-time-rolling-policy.max-file-size", sizeTime.getMaxFileSize());
        if (maxFileSize.getSize() <= 0) {
            throw invalid(PREFIX + "appender.size-time-rolling-policy.max-file-size", "must be greater than 0", sizeTime.getMaxFileSize());
        }
        FileSize sizeTimeTotal = parseFileSize(PREFIX + "appender.size-time-rolling-policy.total-size-cap", sizeTime.getTotalSizeCap());
        requireNonNegative(PREFIX + "appender.size-time-rolling-policy.total-size-cap", sizeTimeTotal.getSize());
        if (sizeTimeTotal.getSize() > 0 && sizeTimeTotal.getSize() < maxFileSize.getSize()) {
            throw invalid(PREFIX + "appender.size-time-rolling-policy.total-size-cap",
                    "must be 0 or greater than or equal to max-file-size", sizeTime.getTotalSizeCap());
        }

        LogbackProperties.TimeRollingPolicy time = appender.getTimeRollingPolicy();
        requireNonNull(PREFIX + "appender.time-rolling-policy.compression-mode", time.getCompressionMode());
        requireNonNegative(PREFIX + "appender.time-rolling-policy.max-history", time.getMaxHistory());
        FileSize timeTotal = parseFileSize(PREFIX + "appender.time-rolling-policy.total-size-cap", time.getTotalSizeCap());
        requireNonNegative(PREFIX + "appender.time-rolling-policy.total-size-cap", timeTotal.getSize());
    }

    public static void validateAsync(LogbackProperties.Async async) {
        Objects.requireNonNull(async, "async must not be null");
        if (async.getQueueSize() < 1) {
            throw invalid(PREFIX + "async.queue-size", "must be greater than 0", async.getQueueSize());
        }
        requireNonNegative(PREFIX + "async.max-flush-time", async.getMaxFlushTime());
        int threshold = async.getDiscardingThreshold();
        if (threshold < -1 || threshold > async.getQueueSize()) {
            throw invalid(PREFIX + "async.discarding-threshold", "must be -1 or between 0 and queue-size", threshold);
        }
    }

    private static FileSize parseFileSize(String propertyName, String value) {
        requireText(propertyName, value);
        try {
            return FileSize.valueOf(value);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(propertyName + " must be a valid file size, but was: " + value, ex);
        }
    }

    private static void requireText(String propertyName, String value) {
        if (value == null || value.isBlank()) {
            throw invalid(propertyName, "must not be blank", value);
        }
    }

    private static void requireNonNull(String propertyName, Object value) {
        if (value == null) {
            throw invalid(propertyName, "must not be null", null);
        }
    }

    private static void requireNonNegative(String propertyName, long value) {
        if (value < 0) {
            throw invalid(propertyName, "must not be negative", value);
        }
    }

    private static IllegalArgumentException invalid(String propertyName, String requirement, Object value) {
        return new IllegalArgumentException(propertyName + " " + requirement + ", but was: " + value);
    }
}
