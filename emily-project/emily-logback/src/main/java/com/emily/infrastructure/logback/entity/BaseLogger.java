package com.emily.infrastructure.logback.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志基础类
 *
 * @author Emily
 * @since 2020/11/14
 */
public class BaseLogger {
    /**
     * 系统编号
     */
    private String systemNumber;
    /**
     * 请求唯一编号
     */
    private String traceId;
    /**
     * 追踪标识
     */
    private String traceTag;
    /**
     * 追踪时间
     */
    private String traceTime;
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
     * 请求URL
     */
    private String url;
    /**
     * 状态码
     */
    private int status;
    /**
     * 描述
     */
    private String message;
    /**
     * 耗时
     */
    private long spentTime;
    /**
     * 请求参数
     */
    private Map<String, Object> inParams = new HashMap<>();

    /**
     * 响应结果
     */
    private Map<String, Object> outParams = new HashMap<>();

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

    public String getSystemNumber() {
        return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
    }

    public long getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    public String getTraceTime() {
        return traceTime;
    }

    public void setTraceTime(String traceTime) {
        this.traceTime = traceTime;
    }

    public Map<String, Object> getInParams() {
        return inParams;
    }

    public void setInParams(Map<String, Object> inParams) {
        this.inParams = inParams;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getOutParams() {
        return outParams;
    }

    public void setOutParams(Map<String, Object> outParams) {
        this.outParams = outParams;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BaseLogger systemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
        return this;
    }

    public BaseLogger traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public BaseLogger traceTag(String traceTag) {
        this.traceTag = traceTag;
        return this;
    }

    public BaseLogger clientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public BaseLogger serverIp(String serverIp) {
        this.serverIp = serverIp;
        return this;
    }

    public BaseLogger appType(String appType) {
        this.appType = appType;
        return this;
    }

    public BaseLogger appVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public BaseLogger url(String url) {
        this.url = url;
        return this;
    }

    public BaseLogger status(int status) {
        this.status = status;
        return this;
    }

    public BaseLogger message(String message) {
        this.message = message;
        return this;
    }

    public BaseLogger traceTime(String traceTime) {
        this.traceTime = traceTime;
        return this;
    }

    public BaseLogger spentTime(long spentTime) {
        this.spentTime = spentTime;
        return this;
    }

    public BaseLogger inParams(Map<String, Object> inParams) {
        this.inParams = inParams;
        return this;
    }

    public BaseLogger inParams(String key, Object value) {
        this.inParams.put(key, value);
        return this;
    }

    public BaseLogger outParams(Map<String, Object> params) {
        this.outParams.putAll(params);
        return this;
    }

    public BaseLogger outParams(String key, Object value) {
        this.outParams.put(key, value);
        return this;
    }
}
