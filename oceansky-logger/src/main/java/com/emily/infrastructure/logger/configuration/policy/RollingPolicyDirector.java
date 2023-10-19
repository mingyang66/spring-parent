package com.emily.infrastructure.logger.configuration.policy;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.RollingPolicyType;

/**
 * 归档策略导演类
 *
 * @author :  Emily
 * @since :  2023/10/19 11:20 PM
 */
public class RollingPolicyDirector {
    private final LoggerContext lc;
    private final LoggerProperties.RollingPolicy rollingPolicy;

    public RollingPolicyDirector(LoggerContext lc, LoggerProperties.RollingPolicy rollingPolicy) {
        this.lc = lc;
        this.rollingPolicy = rollingPolicy;
    }

    public RollingPolicy build(RollingFileAppender<ILoggingEvent> appender, String loggerPath) {
        AbstractRollingPolicy abstractRollingPolicy;
        if (RollingPolicyType.SIZE_AND_TIME_BASED.equals(rollingPolicy.getType())) {
            abstractRollingPolicy = LogbackSizeAndTimeBasedRollingPolicyBuilder.create(lc, rollingPolicy);
        } else if (RollingPolicyType.TIME_BASE.equals(rollingPolicy.getType())) {
            abstractRollingPolicy = LogbackTimeBasedRollingPolicyBuilder.create(lc, rollingPolicy);
        } else {
            abstractRollingPolicy = LogbackFixedWindowRollingPolicyBuilder.create(lc, rollingPolicy);
        }
        return abstractRollingPolicy.getRollingPolicy(appender, loggerPath);
    }

    public static RollingPolicyDirector create(LoggerContext lc, LoggerProperties.RollingPolicy rollingPolicy) {
        return new RollingPolicyDirector(lc, rollingPolicy);
    }
}
