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
     * 追踪类型，0【正常】1【重试】2【重试异常】
     */
    private int traceType;
    /**
     * 类实例
     */
    private Class clazz;
    /**
     * 请求URL
     */
    private String requestUrl;
    /**
     * 请求Method
     */
    private String method;
    /**
     * 协议
     */
    private String protocol;
    /**
     * 请求类型
     */
    private String contentType;
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
    private Object responseBody;
    /**
     * 数据大小
     */
    private String dataSize;

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

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
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

    public int getTraceType() {
        return traceType;
    }

    public void setTraceType(int traceType) {
        this.traceType = traceType;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
