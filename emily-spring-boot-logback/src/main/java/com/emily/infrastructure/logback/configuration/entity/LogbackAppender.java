package com.emily.infrastructure.logback.configuration.entity;

import ch.qos.logback.classic.Level;
import com.emily.infrastructure.common.utils.constant.CharacterUtils;
import com.emily.infrastructure.common.utils.path.PathUtils;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.enumeration.LogbackType;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @program: spring-parent
 * @description: 日志滚动帮助类
 * @author: Emily
 * @create: 2021/07/07
 */
public class LogbackAppender {
    /**
     * appender名称
     */
    private String appenderName;
    /**
     * 日志路径
     */
    private String loggerPath;
    /**
     * 日志文件名
     */
    private String fileName;
    /**
     * appender日志级别
     */
    private Level level;
    /**
     * 日志类型
     */
    private LogbackType logbackType;

    public String getAppenderName() {
        return StringUtils.join(this.appenderName, CharacterUtils.LINE_THROUGH_BOTTOM, this.getLevel().levelStr);
    }

    public void setAppenderName(String appenderName) {
        this.appenderName = appenderName;
    }

    public String getLoggerPath() {
        return loggerPath;
    }

    public void setLoggerPath(String loggerPath) {
        this.loggerPath = loggerPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public LogbackType getLogbackType() {
        return logbackType;
    }

    public void setLogbackType(LogbackType logbackType) {
        this.logbackType = logbackType;
    }

    public LogbackAppender builder(Level level) {
        this.setLevel(level);
        return this;
    }

    public static LogbackAppender toAppender(String appenderName, String loggerPath, String fileName, LogbackType logbackType) {
        LogbackAppender appender = new LogbackAppender();
        appender.setAppenderName(appenderName);
        appender.setLoggerPath(loggerPath);
        appender.setFileName(fileName);
        appender.setLogbackType(logbackType);
        return appender;
    }

    public static LogbackAppender toAppender(String appenderName, String loggerPath, String fileName, Level level, LogbackType logbackType) {
        LogbackAppender appender = new LogbackAppender();
        appender.setAppenderName(appenderName);
        appender.setLoggerPath(loggerPath);
        appender.setFileName(fileName);
        appender.setLevel(level);
        appender.setLogbackType(logbackType);
        return appender;
    }

    /**
     * 获取appender对应文件路径
     *
     * @param properties
     * @return
     */
    public String getFilePath(LogbackProperties properties) {
        //日志文件路径
        String loggerPath;
        if (LogbackType.GROUP.equals(this.getLogbackType())) {
            loggerPath = StringUtils.join(properties.getPath(), PathUtils.normalizePath(this.getLoggerPath()), File.separator, this.getLevel().levelStr.toLowerCase(), File.separator, this.getFileName());
        } else if (LogbackType.MODULE.equals(this.getLogbackType())) {
            loggerPath = StringUtils.join(properties.getPath(), PathUtils.normalizePath(this.getLoggerPath()), File.separator, this.getFileName());
        } else {
            loggerPath = StringUtils.join(properties.getPath(), File.separator, this.getLevel().levelStr.toLowerCase(), File.separator, this.getLevel().levelStr.toLowerCase());
        }
        return loggerPath;
    }

    /**
     * 获取appender文件对应的
     *
     * @param properties
     * @return
     */
    public String getFilePattern(LogbackProperties properties) {
        if (LogbackType.MODULE.equals(this.getLogbackType())) {
            return properties.getModule().getPattern();
        } else if (LogbackType.GROUP.equals(this.getLogbackType())) {
            return properties.getGroup().getPattern();
        } else {
            return properties.getRoot().getPattern();
        }
    }
}
