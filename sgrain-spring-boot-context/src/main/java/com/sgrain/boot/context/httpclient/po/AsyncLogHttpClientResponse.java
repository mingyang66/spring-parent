package com.sgrain.boot.context.httpclient.po;

import com.sgrain.boot.common.base.BaseLog;

import java.util.Date;

/**
 * @program: spring-parent
 * @description: RestTemplate拦截日志实体类
 * @create: 2020/08/24
 */
public class AsyncLogHttpClientResponse extends BaseLog {

    //响应时间
    private Date responseTime;
    //耗时
    private long spentTime;
    //响应结果
    private Object responseBody;
    //数据大小
    private String dataSize;

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

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }
}
