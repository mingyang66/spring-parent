package com.emily.infrastructure.logger.configuration.classic;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.common.CommonKeys;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.LogbackType;
import org.slf4j.Logger;

/**
 * Logback对象导演类
 *
 * @author :  Emily
 * @since :  2023/10/17 9:10 PM
 */
public class LoggerDirector {
    private LoggerProperties properties;
    private LoggerContext loggerContext;

    public LoggerDirector(LoggerProperties properties, LoggerContext loggerContext) {
        this.properties = properties;
        this.loggerContext = loggerContext;
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param commonKeys 属性配置上下文传递类
     */
    public Logger getLogger(CommonKeys commonKeys) {
        AbstractLogback logback;
        if (commonKeys.getLogbackType().equals(LogbackType.MODULE)) {
            logback = LogbackModuleBuilder.create(properties, loggerContext);
        } else if (commonKeys.getLogbackType().equals(LogbackType.GROUP)) {
            logback = LogbackGroupBuilder.create(properties, loggerContext);
        } else {
            logback = LogbackRootBuilder.create(properties, loggerContext);
        }
        return logback.getLogger(commonKeys);
    }

    public static LoggerDirector create(LoggerProperties properties, LoggerContext loggerContext) {
        return new LoggerDirector(properties, loggerContext);
    }
}
