package com.emily.infrastructure.logger.common;


import com.emily.infrastructure.logger.configuration.type.LogbackType;

/**
 * 日志滚动帮助类
 *
 * @author Emily
 * @since : 2021/07/07
 */
public class CommonKeys {
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

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public LogbackType getLogbackType() {
        return logbackType;
    }

    public void setLogbackType(LogbackType logbackType) {
        this.logbackType = logbackType;
    }


}
