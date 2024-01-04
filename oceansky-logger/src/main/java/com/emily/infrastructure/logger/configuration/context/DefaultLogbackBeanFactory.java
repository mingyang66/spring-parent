package com.emily.infrastructure.logger.configuration.context;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import com.emily.infrastructure.logger.common.CommonKeys;
import com.emily.infrastructure.logger.configuration.classic.AbstractLogback;
import com.emily.infrastructure.logger.configuration.classic.LogbackGroup;
import com.emily.infrastructure.logger.configuration.classic.LogbackModule;
import com.emily.infrastructure.logger.configuration.classic.LogbackRoot;
import com.emily.infrastructure.logger.configuration.policy.AbstractRollingPolicy;
import com.emily.infrastructure.logger.configuration.policy.LogbackFixedWindowRollingPolicy;
import com.emily.infrastructure.logger.configuration.policy.LogbackSizeAndTimeBasedRollingPolicy;
import com.emily.infrastructure.logger.configuration.policy.LogbackTimeBasedRollingPolicy;
import com.emily.infrastructure.logger.configuration.property.LoggerConfig;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 默认容器工厂类
 *
 * @author :  Emily
 * @since :  2024/1/1 9:47 AM
 */
public class DefaultLogbackBeanFactory {
    private static final List<AbstractRollingPolicy> POLICIES = new ArrayList<>(3);
    public static final List<AbstractLogback> LOGGERS = new ArrayList<>(3);

    public static void registerBean(LoggerConfig config, LoggerContext lc) {
        POLICIES.add(new LogbackSizeAndTimeBasedRollingPolicy(config, lc));
        POLICIES.add(new LogbackTimeBasedRollingPolicy(config, lc));
        POLICIES.add(new LogbackFixedWindowRollingPolicy(config, lc));

        LOGGERS.add(new LogbackGroup(config, lc));
        LOGGERS.add(new LogbackModule(config, lc));
        LOGGERS.add(new LogbackRoot(config, lc));
    }

    public static RollingPolicy getBean(RollingFileAppender<ILoggingEvent> appender, String loggerPath, LoggerConfig.RollingPolicy rollingPolicy) {
        Optional<AbstractRollingPolicy> policy = POLICIES.stream().filter(l -> l.support(rollingPolicy.getType())).findFirst();
        if (policy.isPresent()) {
            return policy.get().getRollingPolicy(appender, loggerPath);
        }
        throw new IllegalArgumentException("not support rolling policy type: " + rollingPolicy + " , please check your configuration");
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param commonKeys 属性配置上下文传递类
     */
    public static Logger getBean(CommonKeys commonKeys) {
        Optional<AbstractLogback> logback = LOGGERS.stream().filter(l -> l.supports(commonKeys.getLogbackType())).findFirst();
        if (logback.isPresent()) {
            return logback.get().getLogger(commonKeys);
        }
        throw new IllegalArgumentException("非法参数");
    }
}
