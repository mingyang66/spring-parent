package com.emily.infrastructure.logger.configuration.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

/**
 * @author :  Emily
 * @since :  2023/10/18 9:47 PM
 */
public class LogbackSocketAppender {
    /**
     * logger上下文
     */
    private final LoggerContext lc;

    public LogbackSocketAppender(LoggerContext lc) {
        this.lc = lc;
    }

    protected Appender<ILoggingEvent> getAppender() {
        ch.qos.logback.classic.net.SocketAppender appender = new ch.qos.logback.classic.net.SocketAppender();
        appender.setContext(lc);
        appender.setName("socketAppenderName");
        appender.setRemoteHost("172.30.71.32");
        appender.setPort(8100);
        appender.start();
        return appender;
    }


}
