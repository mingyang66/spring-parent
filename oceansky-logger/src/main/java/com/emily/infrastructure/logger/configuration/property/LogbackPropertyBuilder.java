package com.emily.infrastructure.logger.configuration.property;


import com.emily.infrastructure.logger.configuration.type.LogbackType;

/**
 * 日志滚动帮助类
 *
 * @author Emily
 * @since : 2021/07/07
 */
public class LogbackPropertyBuilder {
    /**
     * logger name
     */
    private String loggerName;
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

    public LogbackPropertyBuilder withLoggerName(String loggerName) {
        this.loggerName = loggerName;
        return this;
    }

    public LogbackPropertyBuilder withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public LogbackPropertyBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public LogbackPropertyBuilder withLogbackType(LogbackType logbackType) {
        this.logbackType = logbackType;
        return this;
    }

    public static LogbackPropertyBuilder create() {
        return new LogbackPropertyBuilder();
    }

    public LogbackProperty build() {
        LogbackProperty property = new LogbackProperty();
        property.setLoggerName(this.loggerName);
        property.setFilePath(this.filePath);
        property.setFileName(this.fileName);
        property.setLogbackType(this.logbackType);
        return property;
    }
}
