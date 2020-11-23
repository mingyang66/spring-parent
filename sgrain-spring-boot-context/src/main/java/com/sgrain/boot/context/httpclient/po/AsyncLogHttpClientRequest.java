package com.sgrain.boot.context.httpclient.po;

import com.sgrain.boot.common.base.BaseLog;

import java.util.Date;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: RestTemplate拦截日志实体类
 * @create: 2020/08/24
 */
public class AsyncLogHttpClientRequest extends BaseLog {

    //请求参数
    private Map<String, Object> requestParams;
    //请求时间
    private Date requestTime;

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
}
