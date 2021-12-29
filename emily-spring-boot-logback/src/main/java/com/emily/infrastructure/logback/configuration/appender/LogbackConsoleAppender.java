package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.filter.LogbackFilter;

import java.nio.charset.StandardCharsets;

/**
 * @author Emily
 * @description: 通过名字和级别设置Appender
 * @create: 2020/08/04
 */
public class LogbackConsoleAppender {
    /**
     * logger上下文
     */
    private LoggerContext loggerContext;
    private LogbackProperties properties;

    public LogbackConsoleAppender(LoggerContext loggerContext, LogbackProperties properties) {
        this.loggerContext = loggerContext;
        this.properties = properties;
    }

    /**
     * 控制台打印appender
     *
     * @param level
     * @return
     */
    public ConsoleAppender getConsoleAppender(Level level) {

        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();

        //这里设置级别过滤器
        ThresholdFilter levelFilter = LogbackFilter.getThresholdLevelFilter(level);
        levelFilter.start();

        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setContext(loggerContext);
        //appender的name属性
        appender.setName("console");

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        encoder.setContext(loggerContext);
        //设置格式
        encoder.setPattern(properties.getRoot().getPattern());
        //设置编码格式
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();

        //添加过滤器
        appender.addFilter(levelFilter);
        //设置编码
        appender.setEncoder(encoder);
        appender.start();
        return appender;

    }
}
