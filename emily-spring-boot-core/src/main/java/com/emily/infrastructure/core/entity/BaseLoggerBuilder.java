package com.emily.infrastructure.core.entity;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * 日志基础类建造器
 *
 * @author Emily
 * @since 2020/11/14
 */
public class BaseLoggerBuilder implements Serializable {
    /**
     * 系统编号
     */
    private String systemNumber;
    /**
     * 请求唯一编号
     */
    private String traceId;
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
     * 请求参数
     */
    private Map<String, Object> requestParams = Maps.newHashMap();
    /**
     * 触发时间
     */
    private String triggerTime;
    /**
     * 耗时
     */
    private long spentTime;
    /**
     * 响应结果
     */
    private Object body;

    public BaseLoggerBuilder withSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
        return this;
    }

    public BaseLoggerBuilder withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public BaseLoggerBuilder withClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public BaseLoggerBuilder withServerIp(String serverIp) {
        this.serverIp = serverIp;
        return this;
    }

    public BaseLoggerBuilder withAppType(String appType) {
        this.appType = appType;
        return this;
    }

    public BaseLoggerBuilder withAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public BaseLoggerBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public BaseLoggerBuilder withStatus(int status) {
        this.status = status;
        return this;
    }

    public BaseLoggerBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public BaseLoggerBuilder withRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    public BaseLoggerBuilder withRequestParams(String key, Object value) {
        this.requestParams.put(key, value);
        return this;
    }

    public BaseLoggerBuilder withTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
        return this;
    }

    public BaseLoggerBuilder withSpentTime(long spentTime) {
        this.spentTime = spentTime;
        return this;
    }

    public BaseLoggerBuilder withBody(Object body) {
        this.body = body;
        return this;
    }

    public BaseLogger build() {
        BaseLogger logger = new BaseLogger();
        logger.setSystemNumber(this.systemNumber);
        logger.setTraceId(this.traceId);
        logger.setClientIp(this.clientIp);
        logger.setServerIp(this.serverIp);
        logger.setAppType(this.appType);
        logger.setAppVersion(this.appVersion);
        logger.setUrl(this.url);
        logger.setStatus(this.status);
        logger.setMessage(this.message);
        logger.setRequestParams(this.requestParams);
        logger.setTriggerTime(this.triggerTime);
        logger.setSpentTime(this.spentTime);
        logger.setBody(this.body);
        return logger;
    }
}
