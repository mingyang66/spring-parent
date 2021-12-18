package com.emily.infrastructure.logback.classic;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logback.LogbackProperties;
import org.slf4j.LoggerFactory;

/**
 * @program: spring-parent
 * @description: 日志实现抽象类
 * @author: 姚明洋
 * @create: 2021/12/17
 */
public class AbstractLogback implements Logback {
    private static LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    private LogbackProperties properties;

    public AbstractLogback(LogbackProperties properties) {
        this.properties = properties;
    }


    public LoggerContext getLoggerContext() {
        return loggerContext;
    }

    public LogbackProperties getProperties() {
        return properties;
    }
}
