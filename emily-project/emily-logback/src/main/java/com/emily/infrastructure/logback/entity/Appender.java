package com.emily.infrastructure.logback.entity;

/**
 * @author :  Emily
 * @since :  2026/1/28 下午2:18
 */
public class Appender {
    /**
     * 日志文件存放路径，默认是:./logs
     */
    private String path = "./logs";
    /**
     * 如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
     */
    private boolean append = true;
    /**
     * 如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
     */
    private boolean prudent = false;
    /**
     * 设置是否将输出流刷新，确保日志信息不丢失，默认：true
     */
    private boolean immediateFlush = true;
    /**
     * 文件归档策略
     */
    private RollingPolicy rollingPolicy = new RollingPolicy();
    /**
     * 异步日志配置
     */
    private Async async = new Async();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public boolean isPrudent() {
        return prudent;
    }

    public void setPrudent(boolean prudent) {
        this.prudent = prudent;
    }

    public boolean isImmediateFlush() {
        return immediateFlush;
    }

    public void setImmediateFlush(boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
    }

    public RollingPolicy getRollingPolicy() {
        return rollingPolicy;
    }

    public void setRollingPolicy(RollingPolicy rollingPolicy) {
        this.rollingPolicy = rollingPolicy;
    }

    public Async getAsync() {
        return async;
    }

    public void setAsync(Async async) {
        this.async = async;
    }
}
