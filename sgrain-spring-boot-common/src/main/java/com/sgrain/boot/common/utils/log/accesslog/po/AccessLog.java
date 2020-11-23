package com.sgrain.boot.common.utils.log.accesslog.po;

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
    /**
     * 是否启用日志异步记录Appender
     */
    private boolean enableAsyncAppender;
    /**
     * 队列的最大容量，默认为 256
     */
    private int asyncQueueSize = 256;
    /**
     * 默认，当队列还剩余 20% 的容量时，会丢弃级别为 TRACE, DEBUG 与 INFO 的日志，仅仅只保留 WARN 与 ERROR 级别的日志。想要保留所有的事件，可以设置为 0
     */
    private int asyncDiscardingThreshold;
    /**
     * 根据所引用 appender 队列的深度以及延迟， AsyncAppender 可能会耗费长时间去刷新队列。
     * 当 LoggerContext 被停止时， AsyncAppender stop 方法会等待工作线程指定的时间来完成。
     * 使用 maxFlushTime 来指定最大的刷新时间，单位为毫秒。在指定时间内没有被处理完的事件将会被丢弃。这个属性的值的含义与 Thread.join(long)) 相同
     * 默认是 1000毫秒
     */
    private int asyncMaxFlushTime = 1000;
    /**
     * 在队列满的时候 appender 会阻塞而不是丢弃信息。设置为 true，appender 不会阻塞你的应用而会将消息丢弃，默认为 false
     */
    private boolean asyncNeverBlock;


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

    public boolean isEnableAsyncAppender() {
        return enableAsyncAppender;
    }

    public void setEnableAsyncAppender(boolean enableAsyncAppender) {
        this.enableAsyncAppender = enableAsyncAppender;
    }

    public int getAsyncQueueSize() {
        return asyncQueueSize;
    }

    public void setAsyncQueueSize(int asyncQueueSize) {
        this.asyncQueueSize = asyncQueueSize;
    }

    public int getAsyncDiscardingThreshold() {
        return asyncDiscardingThreshold;
    }

    public void setAsyncDiscardingThreshold(int asyncDiscardingThreshold) {
        this.asyncDiscardingThreshold = asyncDiscardingThreshold;
    }

    public int getAsyncMaxFlushTime() {
        return asyncMaxFlushTime;
    }

    public void setAsyncMaxFlushTime(int asyncMaxFlushTime) {
        this.asyncMaxFlushTime = asyncMaxFlushTime;
    }

    public boolean isAsyncNeverBlock() {
        return asyncNeverBlock;
    }

    public void setAsyncNeverBlock(boolean asyncNeverBlock) {
        this.asyncNeverBlock = asyncNeverBlock;
    }
}
