package com.emily.infrastructure.logger.common;


import com.emily.infrastructure.logger.configuration.type.LogbackType;

/**
 * 日志滚动帮助类
 *
 * @author Emily
 * @since : 2021/07/07
 */
public class CommonKeysBuilder {
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

    public CommonKeysBuilder withLoggerName(String loggerName) {
        this.loggerName = loggerName;
        return this;
    }

    public CommonKeysBuilder withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public CommonKeysBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public CommonKeysBuilder withLogbackType(LogbackType logbackType) {
        this.logbackType = logbackType;
        return this;
    }

    public static CommonKeysBuilder create() {
        return new CommonKeysBuilder();
    }

    public CommonKeys build() {
        CommonKeys property = new CommonKeys();
        property.setLoggerName(this.loggerName);
        property.setFilePath(this.filePath);
        property.setFileName(this.fileName);
        property.setLogbackType(this.logbackType);
        return property;
    }
}
