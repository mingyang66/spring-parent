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
    private LoggerContext lc;

    public LoggerDirector(LoggerProperties properties, LoggerContext lc) {
        this.properties = properties;
        this.lc = lc;
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
            logback = LogbackModule.create(properties, lc);
        } else if (commonKeys.getLogbackType().equals(LogbackType.GROUP)) {
            logback = LogbackGroup.create(properties, lc);
        } else {
            logback = LogbackRoot.create(properties, lc);
        }
        return logback.getLogger(commonKeys);
    }

    public static LoggerDirector create(LoggerProperties properties, LoggerContext lc) {
        return new LoggerDirector(properties, lc);
    }
}
