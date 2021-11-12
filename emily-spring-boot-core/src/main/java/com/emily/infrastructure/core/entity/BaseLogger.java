package com.emily.infrastructure.core.entity;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 日志基础类
 * @create: 2020/11/14
 */
public class BaseLogger implements Serializable {
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
     * 请求URL
     */
    private String url;
    /**
     * 请求Method
     */
    private String method;
    /**
     * 请求参数
     */
    private Map<String, Object> requestParams;
    /**
     * 触发时间
     */
    private String triggerTime;
    /**
     * 耗时
     */
    private long time;
    /**
     * 响应结果
     */
    private Object body;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }

    public void setRequestParams(Object[] params) {
        if (Objects.isNull(params)) {
            return;
        }
        if (Objects.isNull(requestParams)) {
            this.requestParams = new HashMap<>();
        }
        for (int i = 0; i < params.length; i++) {
            this.requestParams.put(MessageFormat.format("arg{0}", i), params[i]);
        }
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
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
}
