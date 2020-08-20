package com.sgrain.boot.autoconfigure.httpclient.interceptor;

import com.google.common.collect.Maps;
import com.sgrain.boot.common.enums.DateFormatEnum;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.UUIDUtils;
import com.sgrain.boot.common.utils.calculation.ObjectSizeUtil;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import com.sgrain.boot.common.utils.date.DateUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: RestTemplate拦截器
 * @create: 2020/08/17
 */
public class HttpClientInterceptor implements ClientHttpRequestInterceptor {

    private static final String THIRD_PARTY = "Third_Party";

    /**
     * RestTemplate拦截方法
     *
     * @param request
     * @param body
     * @param execution
     * @return
     * @throws IOException
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        //生成事物流水号
        String tId = UUIDUtils.randomUUID();
        //记录请求日志
        traceRequest(request, body, tId);
        //新建计时器并开始计时
        StopWatch stopWatch = StopWatch.createStarted();
        //调用接口
        ClientHttpResponse response = execution.execute(request, body);
        //暂停计时
        stopWatch.stop();
        //耗时
        long spendTime = (stopWatch.getTime() == 0) ? 1 : stopWatch.getTime();
        //记录响应日志
        traceResponse(request, body, response, spendTime, tId);
        return response;
    }

    /**
     * @Description 记录请求信息
     * @Version 1.0
     */
    private void traceRequest(HttpRequest request, byte[] body, String tId) {
        //请求类型
        MediaType mediaType = request.getHeaders().getContentType();
        //请求日志记录集合
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("T_ID", tId);
        logMap.put("Request Time", DateUtils.formatDate(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        logMap.put("Request URL", StringUtils.substringBefore(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN));
        logMap.put("Request Method", request.getMethod());
        logMap.put("Request Params", ArrayUtils.isNotEmpty(body) ? RequestUtils.getParameterMap(body) : RequestUtils.convertParameterToMap(StringUtils.substringAfter(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN)));
        logMap.put("Content-Type", Objects.nonNull(mediaType) ? mediaType.toString() : null);
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
    private void traceResponse(HttpRequest request, byte[] body, ClientHttpResponse response, long spendTime, String tId) throws IOException {
        //获取响应数据结果
        Object result = RequestUtils.getResponseBody(StreamUtils.copyToByteArray(response.getBody()));
        //请求类型
        MediaType mediaType = response.getHeaders().getContentType();
        //响应请求信息日志集合
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("T_ID", tId);
        logMap.put("Request Time", DateUtils.formatDate(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        logMap.put("Request URL", StringUtils.substringBefore(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN));
        logMap.put("Request Method", request.getMethod());
        logMap.put("Request Params", ArrayUtils.isNotEmpty(body) ? RequestUtils.getParameterMap(body) : RequestUtils.convertParameterToMap(StringUtils.substringAfter(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN)));
        logMap.put("Content-Type", Objects.nonNull(mediaType) ? mediaType.toString(): null);
        logMap.put("Spend Time", StringUtils.join(spendTime, "ms"));
        logMap.put("Data Size", ObjectSizeUtil.getObjectSizeUnit(result));
        logMap.put("Response Body", result);
        if (LoggerUtils.isDebug()) {
            LoggerUtils.module(HttpClientInterceptor.class, THIRD_PARTY, JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.module(HttpClientInterceptor.class, THIRD_PARTY, JSONUtils.toJSONString(logMap));
        }
    }

}
