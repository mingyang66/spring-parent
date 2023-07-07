package com.emily.infrastructure.logger.configuration.classic;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.LoggerProperties;
import org.slf4j.LoggerFactory;

/**
 * @program: spring-parent
 * @description: 日志实现抽象类
 * @author: Emily
 * @create: 2021/12/17
 */
public class AbstractLogback implements Logback {
    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    private LoggerProperties properties;

    public AbstractLogback(LoggerProperties properties) {
        this.properties = properties;
    }


    public LoggerContext getLoggerContext() {
        return loggerContext;
    }

    public LoggerProperties getProperties() {
        return properties;
    }
}
