package com.emily.boot.common.base;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description: 日志基础类
 * @create: 2020/11/14
 */
public class BaseLog implements Serializable {
    //请求唯一编号
    private String traceId;
    //追踪类型，0【正常】1【重试】2【重试异常】
    private int traceType;
    //请求URL
    private String requestUrl;
    //请求Method
    private String method;
    //协议
    private String protocol;
    //请求类型
    private String contentType;

    public void setBaseLog(BaseLog baseLog){
        this.setTraceId(baseLog.getTraceId());
        this.setTraceType(baseLog.getTraceType());
        this.setRequestUrl(baseLog.getRequestUrl());
        this.setMethod(baseLog.getMethod());
        this.setProtocol(baseLog.getProtocol());
        this.setContentType(baseLog.getContentType());
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
