package com.sgrain.boot.context.httpclient.service.impl;

import com.sgrain.boot.common.utils.log.LoggerUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import com.sgrain.boot.context.httpclient.po.AsyncLogHttpClientRequest;
import com.sgrain.boot.context.httpclient.po.AsyncLogHttpClientResponse;
import com.sgrain.boot.context.httpclient.service.AsyncLogHttpClientService;
import org.springframework.scheduling.annotation.Async;

/**
 * @program: spring-parent
 * @description: RestTemplate日志拦服务类
 * @create: 2020/08/24
 */
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
    @Async
    public void traceRequest(AsyncLogHttpClientRequest asyncLogHttpClient) {
        //请求日志记录集合
     /*   Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Trace_ID", asyncLogHttpClient.getTraceId());
        logMap.put("Trace_Type", asyncLogHttpClient.getTraceType());
        logMap.put("Request Time", DateUtils.formatDate(asyncLogHttpClient.getRequestTime(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat()));
        logMap.put("Request URL", asyncLogHttpClient.getRequestUrl());
        logMap.put("Request Method", asyncLogHttpClient.getMethod());
        logMap.put("Protocol", asyncLogHttpClient.getProtocol());
        logMap.put("Request Params", asyncLogHttpClient.getRequestParams());
        logMap.put("Content-Type", asyncLogHttpClient.getContentType());*/
        if (LoggerUtils.isDebug()) {
            LoggerUtils.module(AsyncLogHttpClientServiceImpl.class, THIRD_PARTY, JSONUtils.toJSONPrettyString(asyncLogHttpClient));
        } else {
            LoggerUtils.module(AsyncLogHttpClientServiceImpl.class, THIRD_PARTY, JSONUtils.toJSONString(asyncLogHttpClient));
        }
    }

    /**
     * @Description 记录响应信息
     * @Version 1.0
     */
    @Override
    @Async
    public void traceResponse(AsyncLogHttpClientResponse asyncLogHttpClient) {
        //响应请求信息日志集合
      /*  Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Trace_ID", asyncLogHttpClient.getTraceId());
        logMap.put("Trace_Type", asyncLogHttpClient.getTraceType());
        logMap.put("Response Time", DateUtils.formatDate(asyncLogHttpClient.getResponseTime(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat()));
        logMap.put("Request URL", asyncLogHttpClient.getRequestUrl());
        logMap.put("Request Method", asyncLogHttpClient.getMethod());
        logMap.put("Protocol", asyncLogHttpClient.getProtocol());
        logMap.put("Request Params", asyncLogHttpClient.getRequestParams());
        logMap.put("Content-Type", asyncLogHttpClient.getContentType());
        logMap.put("Spend Time", StringUtils.join((asyncLogHttpClient.getSpentTime() == 0) ? 1 : asyncLogHttpClient.getSpentTime(), "ms"));
        logMap.put("Data Size", ObjectSizeUtil.getObjectSizeUnit(asyncLogHttpClient.getResponseBody()));
        logMap.put("Response Body", asyncLogHttpClient.getResponseBody());*/
        if (LoggerUtils.isDebug()) {
            LoggerUtils.module(AsyncLogHttpClientServiceImpl.class, THIRD_PARTY, JSONUtils.toJSONPrettyString(asyncLogHttpClient));
        } else {
            LoggerUtils.module(AsyncLogHttpClientServiceImpl.class, THIRD_PARTY, JSONUtils.toJSONString(asyncLogHttpClient));
        }
    }
}
