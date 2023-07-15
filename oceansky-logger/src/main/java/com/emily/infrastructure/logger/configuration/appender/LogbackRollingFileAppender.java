package com.emily.infrastructure.logger.configuration.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.emily.infrastructure.logger.common.PathUtils;
import com.emily.infrastructure.logger.common.StrUtils;
import com.emily.infrastructure.logger.configuration.encoder.LogbackEncoder;
import com.emily.infrastructure.logger.configuration.filter.LogbackFilter;
import com.emily.infrastructure.logger.configuration.policy.LogbackRollingPolicy;
import com.emily.infrastructure.logger.configuration.property.LogbackAppender;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.LogbackType;

import java.io.File;
import java.text.MessageFormat;

/**
 * @author Emily
 * @description: 通过名字和级别设置Appender
 * @create: 2020/08/04
 */
public class LogbackRollingFileAppender extends AbstractAppender {

    private final LogbackAppender appender;
    /**
     * 属性配置
     */
    private final LoggerProperties properties;
    /**
     * logger上下文
     */
    private final LoggerContext loggerContext;

    public LogbackRollingFileAppender(LoggerProperties properties, LoggerContext loggerContext, LogbackAppender appender) {
        this.properties = properties;
        this.loggerContext = loggerContext;
        this.appender = appender;
    }

    /**
     * 获取按照时间归档文件附加器对象
     *
     * @param level 日志级别
     * @return appender
     */
    @Override
    protected Appender<ILoggingEvent> getAppender(Level level) {
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        //日志文件路径
        String loggerPath = this.resolveFilePath(level);
        //设置文件名
        rollingFileAppender.setFile(loggerPath);
        //设置日志文件归档策略
        rollingFileAppender.setRollingPolicy(LogbackRollingPolicy.getSingleton().getRollingPolicy(loggerContext, properties, rollingFileAppender, loggerPath));
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        rollingFileAppender.setContext(loggerContext);
        //appender的name属性
        rollingFileAppender.setName(this.getAppenderName(level));
        //如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
        rollingFileAppender.setAppend(properties.getAppender().isAppend());
        //如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
        rollingFileAppender.setPrudent(properties.getAppender().isPrudent());
        //设置过滤器
        rollingFileAppender.addFilter(LogbackFilter.getSingleton().getLevelFilter(loggerContext, level));
        //设置附加器编码
        rollingFileAppender.setEncoder(LogbackEncoder.getSingleton().getPatternLayoutEncoder(loggerContext, this.resolveFilePattern()));
        //设置是否将输出流刷新，确保日志信息不丢失，默认：true
        rollingFileAppender.setImmediateFlush(properties.getAppender().isImmediateFlush());
        rollingFileAppender.start();
        return rollingFileAppender;
    }

    /**
     * 获取文件路径
     *
     * @param level 日志级别
     * @return 日志文件路径
     */
    @Override
    protected String resolveFilePath(Level level) {
        //基础相对路径
        String basePath = properties.getAppender().getPath();
        //文件路径
        String filePath = PathUtils.normalizePath(appender.getFilePath());
        //日志级别
        String levelStr = level.levelStr.toLowerCase();
        // 基础路径
        String loggerPath = StrUtils.join(basePath, filePath, File.separator);
        //基础日志
        if (LogbackType.ROOT.equals(appender.getLogbackType())) {
            loggerPath = StrUtils.join(loggerPath, levelStr, File.separator, levelStr);
        }
        //分模块日志
        else if (LogbackType.MODULE.equals(appender.getLogbackType())) {
            loggerPath = StrUtils.join(loggerPath, appender.getFileName());
        }
        //分组日志
        else {
            loggerPath = StrUtils.join(loggerPath, levelStr, File.separator, levelStr);
        }
        return StrUtils.substVars(loggerContext, loggerPath, ".log");
    }

    /**
     * 获取日志输出格式
     *
     * @return 日志格式
     */
    @Override
    protected String resolveFilePattern() {
        //基础日志
        if (LogbackType.ROOT.equals(appender.getLogbackType())) {
            return properties.getRoot().getPattern();
        }
        //分组
        if (LogbackType.GROUP.equals(appender.getLogbackType())) {
            return properties.getGroup().getPattern();
        }
        //分模块
        return properties.getModule().getPattern();
    }

    /**
     * 日志级别
     *
     * @param level 日志级别
     * @return appender name值
     */
    @Override
    protected String getAppenderName(Level level) {
        return MessageFormat.format("{0}_{1}", appender.getAppenderName(), level.levelStr.toLowerCase());
    }
}
