package com.emily.infrastructure.tracing.holder;

import java.time.Instant;

/**
 * 上下文实体类
 *
 * @author Emily
 * @since Created in 2023/4/22 3:51 PM
 */
public class TracingHolder {
    /**
     * 事务唯一编号
     */
    private String traceId;
    /**
     * 追踪标识
     */
    private String traceTag;
    /**
     * 系统编号|标识
     */
    private String systemNumber;
    /**
     * 语言
     */
    private String language;
    /**
     * 开启时间
     */
    private Instant startTime;
    /**
     * API接口耗时
     */
    private long spentTime;
    /**
     * 客户端IP
     */
    private String clientIp;
    /**
     * 服务端IP
     */
    private String serverIp;
    /**
     * 版本类型，com.emily.android
     */
    private String appType;
    /**
     * 版本号，4.1.4
     */
    private String appVersion;
    /**
     * (逻辑)是否servlet容器上下文，默认：false
     */
    private boolean servlet;
    /**
     * 当前上下文所处阶段标识
     */
    private TracingPhase tracingPhase;

    public long getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    public TracingPhase getTracingPhase() {
        return tracingPhase;
    }

    public void setTracingPhase(TracingPhase tracingPhase) {
        this.tracingPhase = tracingPhase;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceTag() {
        return traceTag;
    }

    public void setTraceTag(String traceTag) {
        this.traceTag = traceTag;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getSystemNumber() {
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isServlet() {
        return servlet;
    }

    public void setServlet(boolean servlet) {
        this.servlet = servlet;
    }

    public TracingHolder systemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
        return this;
    }

    public TracingHolder traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public TracingHolder language(String language) {
        this.language = language;
        return this;
    }

    public TracingHolder clientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public TracingHolder serverIp(String serverIp) {
        this.serverIp = serverIp;
        return this;
    }

    public TracingHolder appType(String appType) {
        this.appType = appType;
        return this;
    }

    public TracingHolder appVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public TracingHolder servlet(boolean servlet) {
        this.servlet = servlet;
        return this;
    }

    public TracingHolder tracingPhase(TracingPhase tracingPhase) {
        this.tracingPhase = tracingPhase;
        return this;
    }

    public TracingHolder startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public TracingHolder spentTime(long spentTime) {
        this.spentTime = spentTime;
        return this;
    }
}
