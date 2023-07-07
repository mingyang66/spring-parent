package com.emily.infrastructure.logger.configuration.classic;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import org.slf4j.LoggerFactory;

/**
 * @program: spring-parent
 * @description: 日志实现抽象类
 * @author: Emily
 * @create: 2021/12/17
 */
public abstract class AbstractLogback {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    public LoggerContext getLoggerContext() {
        return loggerContext;
    }

    /**
     * 获取Root Logger对象
     *
     * @return
     */
    public Logger getLogger() {
        return null;
    }

    /**
     * 获取Logger对象
     *
     * @param loggerName   logger属性名
     * @param appenderName appender属性名
     * @param filePath     文件路径
     * @param fileName     文件名
     * @return
     */
    public Logger getLogger(String loggerName, String appenderName, String filePath, String fileName) {
        return null;
    }
}
