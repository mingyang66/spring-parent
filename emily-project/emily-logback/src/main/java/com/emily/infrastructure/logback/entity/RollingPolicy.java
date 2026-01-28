package com.emily.infrastructure.logback.entity;

import com.emily.infrastructure.logback.configuration.type.CompressionMode;
import com.emily.infrastructure.logback.configuration.type.RollingPolicyType;

/**
 * 文件归档策略
 */
public class RollingPolicy {
    /**
     * 是否开启基于文件大小和时间的SizeAndTimeBasedRollingPolicy归档策略
     * 默认是基于TimeBasedRollingPolicy的时间归档策略，默认false
     */
    private RollingPolicyType type = RollingPolicyType.TIME_BASE;
    /**
     * 是否在应用程序启动时删除存档，默认：false
     * 是否在应用启动的时候删除历史日志。
     * 如果设置为真，将在启动应用程序时执行档案删除。默认情况下，此属性设置为 false。归档日志移除通常在滚动期间执行。
     * 但是，有些应用程序的存活时间可能不够长，无法触发滚动。因此，对于如此短命的应用程序，删除存档可能永远没有机会执行。
     * 通过将 cleanHistoryOnStart 设置为 true，将在启动 appender 时执行档案删除。
     */
    private boolean cleanHistoryOnStart = false;
    /**
     * 设置要保留的最大存档文件数量，以异步方式删除旧文件,默认 7
     */
    private int maxHistory = 7;
    /**
     * 单个日志文件最大文件大小 KB、MB、GB，默认500MB
     */
    private String maxFileSize = "500MB";
    /**
     * 控制所有归档文件总大小 KB、MB、GB，默认5GB
     */
    private String totalSizeCap = "5GB";
    /**
     * 压缩模式，默认：zip
     * .gz  1/5  10KB压缩后2KB
     * .zip  2/11 11KB压缩后2KB
     */
    private CompressionMode compressionMode = CompressionMode.ZIP;

    public CompressionMode getCompressionMode() {
        return compressionMode;
    }

    public void setCompressionMode(CompressionMode compressionMode) {
        this.compressionMode = compressionMode;
    }

    public RollingPolicyType getType() {
        return type;
    }

    public void setType(RollingPolicyType type) {
        this.type = type;
    }

    public boolean isCleanHistoryOnStart() {
        return cleanHistoryOnStart;
    }

    public void setCleanHistoryOnStart(boolean cleanHistoryOnStart) {
        this.cleanHistoryOnStart = cleanHistoryOnStart;
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
}
