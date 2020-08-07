package com.sgrain.boot.common.accesslog.po;

/**
 * @description: 日志配置属性
 * @create: 2020/08/08
 */
public class AccessLog {
    /**
     * 默认模块名称
     */
    public static final String DEFAULT_MODULE = "default";
    /**
     * 日志级别，ERROR > WARN > INFO > DEBUG, 默认：DEBUG
     */
    private String level = "DEBUG";
    /**
     * 日志文件存放路径，默认是:./logs
     */
    private String path = "./logs";
    /**
     * 是否开启基于文件大小和时间的SizeAndTimeBasedRollingPolicy归档策略
     * 默认是基于TimeBasedRollingPolicy的时间归档策略，默认false
     */
    private boolean enableSizeAndTimeRollingPolicy;
    /**
     * 设置要保留的最大存档文件数,默认 7
     */
    private int maxHistory = 7;
    /**
     * 最大日志文件大小 KB、MB、GB，默认500MB
     */
    private String maxFileSize = "500MB";
    /**
     * 文件总大小限制 KB、MB、GB，默认5GB
     */
    private String totalSizeCap = "5GB";
    /**
     * 通用日志输出格式，默认：[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n
     */
    private String commonPattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n";
    /**
     * 模块日志输出格式，默认：%msg%n
     */
    private String modulePattern = "%msg%n";
    /**
     * 是否将模块日志信息输出到控制台，默认false
     */
    private boolean enableModuleConsule = false;


    public boolean isEnableSizeAndTimeRollingPolicy() {
        return enableSizeAndTimeRollingPolicy;
    }

    public void setEnableSizeAndTimeRollingPolicy(boolean enableSizeAndTimeRollingPolicy) {
        this.enableSizeAndTimeRollingPolicy = enableSizeAndTimeRollingPolicy;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMaxHistory() {
        return maxHistory;
    }

    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getTotalSizeCap() {
        return totalSizeCap;
    }

    public void setTotalSizeCap(String totalSizeCap) {
        this.totalSizeCap = totalSizeCap;
    }

    public String getCommonPattern() {
        return commonPattern;
    }

    public void setCommonPattern(String commonPattern) {
        this.commonPattern = commonPattern;
    }

    public String getModulePattern() {
        return modulePattern;
    }

    public void setModulePattern(String modulePattern) {
        this.modulePattern = modulePattern;
    }


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isEnableModuleConsule() {
        return enableModuleConsule;
    }

    public void setEnableModuleConsule(boolean enableModuleConsule) {
        this.enableModuleConsule = enableModuleConsule;
    }
}
