package com.sgrain.boot.autoconfigure.httpclient.po;

import java.util.Date;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: RestTemplate拦截日志实体类
 * @create: 2020/08/24
 */
public class AsyncLogHttpClient {
    //请求唯一编号
    private String tId;
    //请求URL
    private String requestUrl;
    //请求Method
    private String method;
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

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
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

    public long getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }
}
