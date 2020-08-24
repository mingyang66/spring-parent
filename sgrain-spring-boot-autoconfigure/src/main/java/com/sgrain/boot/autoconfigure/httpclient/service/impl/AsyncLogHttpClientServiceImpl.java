package com.sgrain.boot.autoconfigure.httpclient.service.impl;

import com.google.common.collect.Maps;
import com.sgrain.boot.autoconfigure.httpclient.interceptor.HttpClientInterceptor;
import com.sgrain.boot.autoconfigure.httpclient.po.AsyncLogHttpClient;
import com.sgrain.boot.autoconfigure.httpclient.service.AsyncLogHttpClientService;
import com.sgrain.boot.common.enums.DateFormatEnum;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.calculation.ObjectSizeUtil;
import com.sgrain.boot.common.utils.date.DateUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: spring-parent
 * @description: RestTemplate日志拦服务类
 * @create: 2020/08/24
 */
@Service
public class AsyncLogHttpClientServiceImpl implements AsyncLogHttpClientService {
    /**
     * 第三方接口请求module name
     */
    private static final String THIRD_PARTY = "Third_Party";

    /**
     * @Description 记录请求信息
     * @Version 1.0
     */
    @Override
    public void traceRequest(AsyncLogHttpClient asyncLogHttpClient) {
        //请求日志记录集合
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("T_ID", asyncLogHttpClient.gettId());
        logMap.put("Request Time", DateUtils.formatDate(asyncLogHttpClient.getRequestTime(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat()));
        logMap.put("Request URL", asyncLogHttpClient.getRequestUrl());
        logMap.put("Request Method", asyncLogHttpClient.getMethod());
        logMap.put("Request Params", asyncLogHttpClient.getRequestParams());
        logMap.put("Content-Type", asyncLogHttpClient.getContentType());
        if (LoggerUtils.isDebug()) {
            LoggerUtils.module(HttpClientInterceptor.class, THIRD_PARTY, JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.module(HttpClientInterceptor.class, THIRD_PARTY, JSONUtils.toJSONString(logMap));
        }
    }

    /**
     * @Description 记录响应信息
     * @Version 1.0
     */
    @Override
    public void traceResponse(AsyncLogHttpClient asyncLogHttpClient) {
        //响应请求信息日志集合
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("T_ID", asyncLogHttpClient.gettId());
        logMap.put("Response Time", DateUtils.formatDate(asyncLogHttpClient.getResponseTime(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat()));
        logMap.put("Request URL", asyncLogHttpClient.getRequestUrl());
        logMap.put("Request Method", asyncLogHttpClient.getMethod());
        logMap.put("Request Params", asyncLogHttpClient.getRequestParams());
        logMap.put("Content-Type", asyncLogHttpClient.getContentType());
        logMap.put("Spend Time", StringUtils.join((asyncLogHttpClient.getSpentTime() == 0) ? 1 : asyncLogHttpClient.getSpentTime(), "ms"));
        logMap.put("Data Size", ObjectSizeUtil.getObjectSizeUnit(asyncLogHttpClient.getResponseBody()));
        logMap.put("Response Body", asyncLogHttpClient.getResponseBody());
        if (LoggerUtils.isDebug()) {
            LoggerUtils.module(HttpClientInterceptor.class, THIRD_PARTY, JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.module(HttpClientInterceptor.class, THIRD_PARTY, JSONUtils.toJSONString(logMap));
        }
    }
}
