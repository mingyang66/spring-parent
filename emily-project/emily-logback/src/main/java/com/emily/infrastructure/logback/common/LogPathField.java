package com.emily.infrastructure.logback.common;


import com.emily.infrastructure.logback.configuration.type.LogbackType;

/**
 * 日志滚动帮助类
 *
 * @author Emily
 * @since : 2021/07/07
 */
public class LogPathField {
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

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public LogbackType getLogbackType() {
        return logbackType;
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

        public LogPathField build() {
            LogPathField field = new LogPathField();
            field.loggerName = this.loggerName;
            field.filePath = this.filePath;
            field.fileName = this.fileName;
            field.logbackType = this.logbackType;
            return field;
        }
    }
}
