package com.emily.infrastructure.logger.configuration.policy;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import com.emily.infrastructure.logger.configuration.type.RollingPolicyType;

/**
 * 归档策略
 *
 * @author :  Emily
 * @since :  2023/10/19 11:32 PM
 */
public abstract class AbstractRollingPolicy {
    /**
     * 判定是否支持该归档策略
     *
     * @param type 归档策略类型
     * @return true-支持，false-不支持
     */
    public boolean support(RollingPolicyType type) {
        return false;
    }

    /**
     * 获取基于时间和大小的日志文件归档策略
     *
     * @param appender   归档文件appender
     * @param loggerPath 日志文件路径
     * @return 归档策略
     */
    public abstract RollingPolicy getRollingPolicy(RollingFileAppender<ILoggingEvent> appender, String loggerPath);
}
