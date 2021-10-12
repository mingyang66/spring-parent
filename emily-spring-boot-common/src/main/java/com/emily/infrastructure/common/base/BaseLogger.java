package com.emily.infrastructure.common.base;

import java.io.Serializable;
import java.util.Map;

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
     * 类实例
     */
    private Class clazz;
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

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

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
}
