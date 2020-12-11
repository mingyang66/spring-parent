package com.emily.boot.context.apilog.po;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/08/22
 */
public class AsyncLogAop implements Serializable {
    //请求唯一编号
    private String tId;
    //类实例
    private Class clazz;
    //控制器方法名
    private String methodName;
    //请求URL
    private String requestUrl;
    //请求Method
    private String method;
    //请求协议
    private String protocol;
    //请求类型
    private String contentType;
    //请求参数
    private Map<String, Object> requestParams;
    //请求时间
    private Date requestTime;
    //响应时间
    private Date responseTime;
    //耗时
    private long spentTime;
    //响应结果
    private Object responseBody;
    //异常
    private String exception;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }

    public long getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }
}
