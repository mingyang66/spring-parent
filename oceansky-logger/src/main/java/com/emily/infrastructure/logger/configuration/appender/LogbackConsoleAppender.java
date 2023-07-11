package com.emily.infrastructure.logger.configuration.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import com.emily.infrastructure.logger.configuration.encoder.LogbackEncoder;
import com.emily.infrastructure.logger.configuration.filter.LogbackFilter;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;

/**
 * @author Emily
 * @description: 通过名字和级别设置Appender
 * @create: 2020/08/04
 */
public class LogbackConsoleAppender extends AbstractAppender {
    /**
     * 控制台appender name
     */
    public static final String CONSOLE_NAME = "CONSOLE";
    /**
     * 属性配置
     */
    private final LoggerProperties properties;
    /**
     * 属性配置
     */
    private final LoggerContext loggerContext;

    public LogbackConsoleAppender(LoggerProperties properties, LoggerContext loggerContext) {
        this.properties = properties;
        this.loggerContext = loggerContext;
    }

    /**
     * 控制台打印appender
     *
     * @param level 日志级别
     * @return consul appender
     */
    @Override
    protected Appender<ILoggingEvent> getAppender(Level level) {
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setContext(loggerContext);
        //appender的name属性
        appender.setName(this.getAppenderName(level));
        //添加过滤器
        appender.addFilter(LogbackFilter.newThresholdLevelFilter(level));
        //设置编码
        appender.setEncoder(LogbackEncoder.newPatternLayoutEncoder(loggerContext, this.resolveFilePattern()));
        //ANSI color codes支持，默认：false；请注意，基于Unix的操作系统（如Linux和Mac OS X）默认支持ANSI颜色代码。
        appender.setWithJansi(true);
        appender.start();
        return appender;

    }

    @Override
    protected String resolveFilePath(Level level) {
        return null;
    }

    @Override
    protected String resolveFilePattern() {
        return properties.getRoot().getConsolePattern();
    }

    @Override
    protected String getAppenderName(Level level) {
        return CONSOLE_NAME;
    }
}
