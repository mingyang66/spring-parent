package com.emily.infrastructure.logger.configuration.policy;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;

/**
 * 归档策略
 *
 * @author :  Emily
 * @since :  2023/10/19 11:32 PM
 */
public abstract class AbstractRollingPolicy {
    /**
     * 获取基于时间和大小的日志文件归档策略
     *
     * @param appender   归档文件appender
     * @param loggerPath 日志文件路径
     * @return 归档策略
     */
    protected abstract RollingPolicy getRollingPolicy(RollingFileAppender<ILoggingEvent> appender, String loggerPath);
}
