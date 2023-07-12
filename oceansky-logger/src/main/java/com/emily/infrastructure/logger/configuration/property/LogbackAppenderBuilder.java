package com.emily.infrastructure.logger.configuration.property;


import com.emily.infrastructure.logger.configuration.type.LogbackType;

/**
 * @program: spring-parent
 * @description: 日志滚动帮助类
 * @author: Emily
 * @create: 2021/07/07
 */
public class LogbackAppenderBuilder {
    /**
     * appender名称
     */
    private String appenderName;
    /**
     * 日志路径
     */
    private String filePath;
    /**
     * 日志文件名
     */
    private String fileName;
    /**
     * 日志类型
     */
    private LogbackType logbackType;

    public LogbackAppenderBuilder withAppenderName(String appenderName) {
        this.appenderName = appenderName;
        return this;
    }

    public LogbackAppenderBuilder withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public LogbackAppenderBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public LogbackAppenderBuilder withLogbackType(LogbackType logbackType) {
        this.logbackType = logbackType;
        return this;
    }

    public LogbackAppender build() {
        LogbackAppender appender = new LogbackAppender();
        appender.setAppenderName(this.appenderName);
        appender.setFilePath(this.filePath);
        appender.setFileName(this.fileName);
        appender.setLogbackType(this.logbackType);
        return appender;
    }
}
