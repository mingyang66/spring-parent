package com.emily.infrastructure.logback.common;


import com.emily.infrastructure.logback.configuration.type.LogbackType;

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

    public static Builder newBuilder() {
        return new Builder();
    }

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

    public static class Builder {
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

        public Builder withLoggerName(String loggerName) {
            this.loggerName = loggerName;
            return this;
        }

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withLogbackType(LogbackType logbackType) {
            this.logbackType = logbackType;
            return this;
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
}
