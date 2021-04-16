package com.emily.framework.context.httpclient.service.impl;

import com.emily.framework.common.utils.json.JSONUtils;
import com.emily.framework.context.logger.LoggerUtils;
import com.emily.framework.context.httpclient.po.AsyncLogHttpClientRequest;
import com.emily.framework.context.httpclient.po.AsyncLogHttpClientResponse;
import com.emily.framework.context.httpclient.service.AsyncLogHttpClientService;
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
        if (LoggerUtils.isDebug()) {
            LoggerUtils.module(AsyncLogHttpClientServiceImpl.class, THIRD_PARTY, JSONUtils.toJSONPrettyString(asyncLogHttpClient));
        } else {
            LoggerUtils.module(AsyncLogHttpClientServiceImpl.class, THIRD_PARTY, JSONUtils.toJSONString(asyncLogHttpClient));
        }
    }
}
